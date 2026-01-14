package com.aleksagn.playlistmaker.presentation.player

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aleksagn.playlistmaker.domain.api.FavoriteTracksInteractor
import com.aleksagn.playlistmaker.domain.api.PlayerInteractor
import com.aleksagn.playlistmaker.domain.api.PlaylistsInteractor
import com.aleksagn.playlistmaker.domain.models.Playlist
import com.aleksagn.playlistmaker.domain.models.Track
import com.aleksagn.playlistmaker.presentation.library.PlaylistsState
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

class PlayerViewModel(
    private val playerInteractor: PlayerInteractor,
    private val favoriteTracksInteractor: FavoriteTracksInteractor,
    private val playlistsInteractor: PlaylistsInteractor,
) : ViewModel() {

    companion object {
        private const val DELAY = 300L
    }

    private var timerJob: Job? = null
    private var track: Track? = null

    private val playerStateLiveData = MutableLiveData<PlayerState>(PlayerState.Default())
    fun observePlayerState(): LiveData<PlayerState> = playerStateLiveData

    private val isFavoriteLiveData = MutableLiveData(false)
    val observeIsFavorite: LiveData<Boolean> = isFavoriteLiveData

    private val playlistStateLiveData = MutableLiveData<PlaylistsState>()
    fun observePlaylistState(): LiveData<PlaylistsState> = playlistStateLiveData

    override fun onCleared() {
        super.onCleared()
        releasePlayer()
    }

    private fun releasePlayer() {
        stopTimer()
        playerInteractor.releasePlayer()
        playerStateLiveData.value = PlayerState.Default()
    }

    fun onPause() {
        pausePlayer()
    }

    fun onPlayButtonClicked() {
        when(playerStateLiveData.value) {
            is PlayerState.Prepared, is PlayerState.Paused -> {
                startPlayer()
            }
            is PlayerState.Playing -> {
                pausePlayer()
            }
            else -> { }
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

        val onPrepare = {
            playerStateLiveData.postValue(PlayerState.Prepared())
        }

        val onComplete = {
            stopTimer()
            playerStateLiveData.postValue(PlayerState.Prepared())
        }

        playerInteractor.preparePlayer(currentTrack.previewUrl, onPrepare, onComplete)
    }

    private fun startPlayer() {
        playerInteractor.startPlayer()
        playerStateLiveData.postValue(PlayerState.Playing(playerInteractor.getCurrentPlayTime()))
        startTimer()
    }

    private fun pausePlayer() {
        playerStateLiveData.postValue(PlayerState.Paused(playerInteractor.getCurrentPlayTime()))
        playerInteractor.pausePlayer()
        stopTimer()
    }

    private fun startTimer() {
        stopTimer()
        timerJob = viewModelScope.launch {
            while (isActive && playerInteractor.isPlaying()) {
                playerStateLiveData.postValue(PlayerState.Playing(playerInteractor.getCurrentPlayTime()))
                delay(DELAY)
            }
        }
    }

    private fun stopTimer() {
        timerJob?.cancel()
        timerJob = null
    }
}
