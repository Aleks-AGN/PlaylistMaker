package com.aleksagn.playlistmaker.ui.player.view_model

import android.os.Handler
import android.os.Looper
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.aleksagn.playlistmaker.domain.api.PlayerInteractor

class PlayerViewModel(private val playerInteractor: PlayerInteractor) : ViewModel() {

    companion object {
        private const val DELAY = 250L
    }

    private val playerStateLiveData = MutableLiveData<PlayerState>()
    fun observePlayerState(): LiveData<PlayerState> = playerStateLiveData

    private var handler = Handler(Looper.getMainLooper())

    private val updatePlayTimerRunnable = updatePlayTimer()

    override fun onCleared() {
        super.onCleared()
        playerInteractor.releasePlayer()
        handler.removeCallbacks(updatePlayTimerRunnable)
    }

    fun onPlayButtonClicked() {
        startPlayer()
    }

    fun onPauseButtonClicked() {
        pausePlayer()
    }

    fun onPause() {
        pausePlayer()
    }

    fun preparePlayer(url: String) {
        val onPrepare = {
            playerStateLiveData.postValue(PlayerState.PreparePlayer)
        }

        val onComplete = {
            handler.removeCallbacks(updatePlayTimerRunnable)
            playerStateLiveData.postValue(PlayerState.CompletePlayer)
        }

        playerInteractor.preparePlayer(url, onPrepare, onComplete)
    }

    private fun startPlayer() {
        playerInteractor.startPlayer()
        playerStateLiveData.postValue(PlayerState.StartPlayer)
        handler.post(updatePlayTimerRunnable)
    }

    private fun pausePlayer() {
        playerStateLiveData.postValue(PlayerState.PausePlayer)
        playerInteractor.pausePlayer()
        handler.removeCallbacks(updatePlayTimerRunnable)
    }

    private fun updatePlayTimer(): Runnable {
        return object : Runnable {
            override fun run() {
                playerStateLiveData.postValue(PlayerState.PlayingPlayer(playerInteractor.getCurrentPlayTime()))
                handler.postDelayed(this, DELAY)
            }
        }
    }
}
