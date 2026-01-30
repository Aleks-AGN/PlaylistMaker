package com.aleksagn.playlistmaker.presentation.player

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aleksagn.playlistmaker.domain.api.FavoriteTracksInteractor
import com.aleksagn.playlistmaker.domain.api.PlaylistsInteractor
import com.aleksagn.playlistmaker.domain.models.Playlist
import com.aleksagn.playlistmaker.domain.models.Track
import com.aleksagn.playlistmaker.presentation.library.PlaylistsState
import com.aleksagn.playlistmaker.services.AudioPlayerControl
import kotlinx.coroutines.launch

class PlayerViewModel(
    private val favoriteTracksInteractor: FavoriteTracksInteractor,
    private val playlistsInteractor: PlaylistsInteractor,
) : ViewModel() {

    private var track: Track? = null

    private val playerStateLiveData = MutableLiveData<PlayerState>(PlayerState.Default())
    fun observePlayerState(): LiveData<PlayerState> = playerStateLiveData

    private val isFavoriteLiveData = MutableLiveData(false)
    val observeIsFavorite: LiveData<Boolean> = isFavoriteLiveData

    private val playlistStateLiveData = MutableLiveData<PlaylistsState>()
    fun observePlaylistState(): LiveData<PlaylistsState> = playlistStateLiveData

    private var audioPlayerControl: AudioPlayerControl? = null

    fun setAudioPlayerControl(audioPlayerControl: AudioPlayerControl) {
        this.audioPlayerControl = audioPlayerControl

        viewModelScope.launch {
            audioPlayerControl.getCurrentPlayerState().collect {
                playerStateLiveData.postValue(it)
            }
        }
    }

    fun removeAudioPlayerControl() {
        audioPlayerControl = null
    }

    fun showNotification() {
        audioPlayerControl?.showNotification()
    }

    fun hideNotification() {
        audioPlayerControl?.hideNotification()
    }

    override fun onCleared() {
        super.onCleared()
        audioPlayerControl = null
    }

    fun onPlayButtonClicked() {
        if (playerStateLiveData.value is PlayerState.Playing) {
            audioPlayerControl?.pausePlayer()
        } else {
            audioPlayerControl?.startPlayer()
        }
    }

    fun onFavoriteButtonClicked() {
        if (isFavoriteLiveData.value == true) {
            viewModelScope.launch {
                favoriteTracksInteractor.deleteFavoriteTrackById(track!!.trackId)
            }
            track!!.isFavorite = false
        } else {
            viewModelScope.launch {
                favoriteTracksInteractor.insertFavoriteTrack(track!!)
            }
            track!!.isFavorite = true
        }
        isFavoriteLiveData.postValue(track!!.isFavorite)
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

    fun observeTrackInPlaylist(track: Track, playlist: Playlist): Boolean {
        return playlistsInteractor.observeTrackInPlaylistById(track.trackId, playlist.playlistId)
    }

    fun addTrackToPlaylist(track: Track, playlist: Playlist) {
        viewModelScope.launch {
            playlistsInteractor.insertTrackToPlaylist(track, playlist.playlistId)
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
        playlistStateLiveData.postValue(state)
    }

    fun preparePlayer(currentTrack: Track) {
        track = currentTrack

        viewModelScope.launch {
            favoriteTracksInteractor.observeFavoriteTrackById(currentTrack.trackId).collect {
                isFavoriteLiveData.postValue(it)
                track!!.isFavorite = it
            }
        }
    }
}
