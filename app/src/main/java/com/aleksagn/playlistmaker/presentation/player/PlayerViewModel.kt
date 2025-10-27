package com.aleksagn.playlistmaker.presentation.player

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aleksagn.playlistmaker.domain.api.PlayerInteractor
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

class PlayerViewModel(private val playerInteractor: PlayerInteractor) : ViewModel() {

    companion object {
        private const val DELAY = 300L
    }

    private var timerJob: Job? = null

    private val playerStateLiveData = MutableLiveData<PlayerState>(PlayerState.Default())
    fun observePlayerState(): LiveData<PlayerState> = playerStateLiveData

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
            else -> { }
        }
    }

    fun onPauseButtonClicked() {
        when(playerStateLiveData.value) {
            is PlayerState.Playing -> {
                pausePlayer()
            }
            else -> { }
        }
    }

    fun preparePlayer(url: String) {
        val onPrepare = {
            playerStateLiveData.postValue(PlayerState.Prepared())
        }

        val onComplete = {
            stopTimer()
            playerStateLiveData.postValue(PlayerState.Prepared())
        }

        playerInteractor.preparePlayer(url, onPrepare, onComplete)
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
