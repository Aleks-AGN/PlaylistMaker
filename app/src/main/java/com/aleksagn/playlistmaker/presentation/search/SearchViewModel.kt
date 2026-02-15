package com.aleksagn.playlistmaker.presentation.search

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aleksagn.playlistmaker.R
import com.aleksagn.playlistmaker.domain.api.SearchHistoryInteractor
import com.aleksagn.playlistmaker.domain.api.TracksInteractor
import com.aleksagn.playlistmaker.domain.models.Track
import com.aleksagn.playlistmaker.util.debounce
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class SearchViewModel(
    private val tracksInteractor: TracksInteractor,
    private val searchHistoryInteractor: SearchHistoryInteractor,
    private val context: Context
) : ViewModel() {

    companion object {
        private const val SEARCH_DEBOUNCE_DELAY = 2000L
    }

//    private val stateLiveData = MutableLiveData<SearchState>()
//    fun observeState(): LiveData<SearchState> = stateLiveData

    private val _state = MutableStateFlow<SearchState>(SearchState.Empty(""))
    val state: StateFlow<SearchState> = _state.asStateFlow()

    private val _query = MutableStateFlow<String>("")
    val query: StateFlow<String> = _query.asStateFlow()

//    private val showToast = SingleLiveEvent<String?>()
//    fun observeShowToast(): LiveData<String?> = showToast

    val toastMessage = MutableStateFlow<String?>(null)
    fun clearToast() { toastMessage.value = null }

    private var latestSearchText: String? = null

    init {
        viewModelScope.launch {
            searchHistoryInteractor
                .getTracksHistory()
                .collect { tracks ->
//                    stateLiveData.value = SearchState.History(tracks)
                    _state.value = SearchState.History(tracks)
                }
        }
    }

    fun getTracksHistory() {
        viewModelScope.launch {
            searchHistoryInteractor
                .getTracksHistory()
                .collect { tracks ->
//                    stateLiveData.value = SearchState.History(tracks)
                    _state.value = SearchState.History(tracks)
                }
        }
    }

    fun saveTrackToHistory(track: Track) {
        searchHistoryInteractor.saveTrackToHistory(track)
//        viewModelScope.launch {
//            searchHistoryInteractor
//                .getTracksHistory()
//                .collect { tracks ->
////                    stateLiveData.value = SearchState.History(tracks)
//                    _state.value = SearchState.History(tracks)
//                }
//        }
    }

    fun clearTracksHistory() {
        searchHistoryInteractor.clearTracksHistory()
        viewModelScope.launch {
            searchHistoryInteractor
                .getTracksHistory()
                .collect { tracks ->
//                    stateLiveData.value = SearchState.History(tracks)
                    _state.value = SearchState.History(tracks)
                }
        }
    }

    fun searchQuick(changedText: String) {
        _query.value = changedText
        this.latestSearchText = changedText
        searchRequest(changedText)
    }

    private val trackSearchDebounce = debounce<String>(SEARCH_DEBOUNCE_DELAY, viewModelScope, true) { changedText ->
        searchRequest(changedText)
    }

    fun searchDebounce(changedText: String) {
        _query.value = changedText
        if (latestSearchText != changedText) {
            latestSearchText = changedText
            trackSearchDebounce(changedText)
        }
    }

    private fun searchRequest(newSearchText: String) {
        if (newSearchText.isNotEmpty()) {

            renderState(SearchState.Loading)

            viewModelScope.launch {
                tracksInteractor
                    .searchTracks(newSearchText)
                    .collect { pair ->
                        processResult(pair.first, pair.second)
                    }
            }
        }
    }

    private fun processResult(foundTracks: List<Track>?, errorMessage: String?) {
        val tracks = mutableListOf<Track>()
        if (foundTracks != null) {
            tracks.addAll(foundTracks)
        }

        when {
            errorMessage != null -> {
                renderState(SearchState.Error(errorMessage = context.getString(R.string.net_connection_error)))
//                showToast.postValue(errorMessage)
                toastMessage.value = errorMessage
            }

            tracks.isEmpty() -> {
                renderState(SearchState.Empty(message = context.getString(R.string.empty_search)))
            }

            else -> {
                renderState(SearchState.Content(tracks = tracks))
            }
        }
    }

    private fun renderState(state: SearchState) {
//        stateLiveData.postValue(state)
        _state.value = state
    }
}
