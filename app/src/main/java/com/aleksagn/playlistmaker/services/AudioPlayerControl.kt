package com.aleksagn.playlistmaker.services

import com.aleksagn.playlistmaker.presentation.player.PlayerState
import kotlinx.coroutines.flow.StateFlow

interface AudioPlayerControl {
    fun startPlayer()
    fun pausePlayer()
    fun getCurrentPlayerState(): StateFlow<PlayerState>
    fun showNotification()
    fun hideNotification()
}