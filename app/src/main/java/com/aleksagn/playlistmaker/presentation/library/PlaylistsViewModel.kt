package com.aleksagn.playlistmaker.presentation.library

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aleksagn.playlistmaker.domain.api.PlaylistsInteractor
import com.aleksagn.playlistmaker.domain.models.Playlist
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class PlaylistsViewModel(
    private val playlistsInteractor: PlaylistsInteractor,
) : ViewModel() {

    private val stateLiveData = MutableLiveData<PlaylistsState>()
    fun observeState(): LiveData<PlaylistsState> = stateLiveData

    private val _state = MutableStateFlow<PlaylistsState>(PlaylistsState.Empty)
    val state: StateFlow<PlaylistsState> = _state.asStateFlow()

    init {
        getPlaylists()
    }

    fun getPlaylists() {
        viewModelScope.launch {
            playlistsInteractor
                .getPlaylists()
                .collect { playlists ->
                    processResult(playlists)
                }
        }
    }

    private fun processResult(playlists: List<Playlist>) {
        if (playlists.isEmpty()) {
            renderState(PlaylistsState.Empty)
        } else {
            renderState(PlaylistsState.Content(playlists))
        }
    }

    private fun renderState(state: PlaylistsState) {
        stateLiveData.postValue(state)
        _state.value = state
    }
}
