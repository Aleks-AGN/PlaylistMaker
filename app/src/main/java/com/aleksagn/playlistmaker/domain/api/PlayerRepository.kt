package com.aleksagn.playlistmaker.domain.api

interface PlayerRepository {
    fun preparePlayer(previewUrl: String, onPrepare: () -> Unit, onComplete: () -> Unit)
    fun startPlayer()
    fun pausePlayer()
    fun getCurrentPlayTime(): String
    fun releasePlayer()
}