package com.aleksagn.playlistmaker.presentation.library

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aleksagn.playlistmaker.R
import com.aleksagn.playlistmaker.domain.api.FavoriteTracksInteractor
import com.aleksagn.playlistmaker.domain.models.Track
import kotlinx.coroutines.launch

class FavoritesViewModel(
    private val favoriteTracksInteractor: FavoriteTracksInteractor,
    private val context: Context
) : ViewModel() {

    private val stateLiveData = MutableLiveData<FavoritesState>()
    fun observeState(): LiveData<FavoritesState> = stateLiveData

    init {
        getFavoriteTracks()
    }

    fun getFavoriteTracks() {
        viewModelScope.launch {
            favoriteTracksInteractor
                .getFavoriteTracks()
                .collect { tracks ->
                    processResult(tracks)
                }
        }
    }

    private fun processResult(tracks: List<Track>) {
        if (tracks.isEmpty()) {
            renderState(FavoritesState.Empty(message = context.getString(R.string.empty_media_library)))
        } else {
            renderState(FavoritesState.Content(tracks))
        }
    }

    private fun renderState(state: FavoritesState) {
        stateLiveData.postValue(state)
    }
}
