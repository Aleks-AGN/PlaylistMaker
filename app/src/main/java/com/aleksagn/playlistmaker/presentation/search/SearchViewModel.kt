package com.aleksagn.playlistmaker.presentation.search

import android.content.Context
import android.os.Handler
import android.os.Looper
import android.os.SystemClock
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.aleksagn.playlistmaker.R
import com.aleksagn.playlistmaker.domain.api.SearchHistoryInteractor
import com.aleksagn.playlistmaker.domain.api.TracksInteractor
import com.aleksagn.playlistmaker.domain.models.Track

class SearchViewModel(
    private val tracksInteractor: TracksInteractor,
    private val searchHistoryInteractor: SearchHistoryInteractor,
    private val context: Context
) : ViewModel() {

    companion object {
        private const val SEARCH_DEBOUNCE_DELAY = 2000L
        private val SEARCH_REQUEST_TOKEN = Any()
    }

    private val stateLiveData = MutableLiveData<SearchState>()
    fun observeState(): LiveData<SearchState> = stateLiveData

    private val showToast = SingleLiveEvent<String?>()
    fun observeShowToast(): LiveData<String?> = showToast

    private var latestSearchText: String? = null

    private val handler = Handler(Looper.getMainLooper())

    init {
        stateLiveData.value = SearchState.History( tracks = searchHistoryInteractor.getTracksHistory() )
    }

    fun saveTrackToHistory(track: Track) {
        searchHistoryInteractor.saveTrackToHistory(track)
        stateLiveData.value = SearchState.History( tracks = searchHistoryInteractor.getTracksHistory() )
    }

    fun clearTracksHistory() {
        searchHistoryInteractor.clearTracksHistory()
        stateLiveData.value = SearchState.History( tracks = searchHistoryInteractor.getTracksHistory() )
    }

    fun searchQuick(changedText: String) {
        this.latestSearchText = changedText
        searchRequest(changedText)
    }

    fun searchDebounce(changedText: String) {
        if (latestSearchText == changedText) {
            return
        }

        this.latestSearchText = changedText
        handler.removeCallbacksAndMessages(SEARCH_REQUEST_TOKEN)

        val searchRunnable = Runnable { searchRequest(changedText) }

        val postTime = SystemClock.uptimeMillis() + SEARCH_DEBOUNCE_DELAY
        handler.postAtTime(
            searchRunnable,
            SEARCH_REQUEST_TOKEN,
            postTime,
        )
    }

    private fun searchRequest(newSearchText: String) {
        if (newSearchText.isNotEmpty()) {
            renderState(
                SearchState.Loading
            )

            tracksInteractor.searchTracks(newSearchText, object : TracksInteractor.TracksConsumer {
                    override fun consume(foundTracks: List<Track>?, errorMessage: String?) {
                        handler.post {
                            val tracks = mutableListOf<Track>()
                            if (foundTracks != null) {
                                tracks.addAll(foundTracks)
                            }

                            when {
                                errorMessage != null -> {
                                    renderState(
                                        SearchState.Error(
                                            errorMessage = context.getString(R.string.net_error),
                                        )
                                    )
                                    showToast.postValue(errorMessage)
                                }

                                tracks.isEmpty() -> {
                                    renderState(
                                        SearchState.Empty(
                                            message = context.getString(R.string.empty_search),
                                        )
                                    )
                                }

                                else -> {
                                    renderState(
                                        SearchState.Content(
                                            tracks = tracks,
                                        )
                                    )
                                }
                            }
                        }
                    }
                })
        }
    }

    private fun renderState(state: SearchState) {
        stateLiveData.postValue(state)
    }

    override fun onCleared() {
        super.onCleared()
        handler.removeCallbacksAndMessages(SEARCH_REQUEST_TOKEN)
    }
}
