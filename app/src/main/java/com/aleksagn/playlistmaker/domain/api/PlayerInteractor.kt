package com.aleksagn.playlistmaker.domain.api

interface PlayerInteractor {
    fun preparePlayer(previewUrl: String, onPrepare: () -> Unit, onComplete: () -> Unit)

    fun startPlayer()

    fun pausePlayer()

    fun getCurrentPlayTime(): String

    fun releasePlayer()
}
