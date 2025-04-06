package com.aleksagn.playlistmaker.domain.impl

import com.aleksagn.playlistmaker.domain.api.PlayerInteractor
import com.aleksagn.playlistmaker.domain.api.PlayerRepository

class PlayerInteractorImpl(private val playerRepository: PlayerRepository) : PlayerInteractor {
    override fun preparePlayer(previewUrl: String, onPrepare: () -> Unit, onComplete: () -> Unit) {
        playerRepository.preparePlayer(previewUrl,onPrepare, onComplete)
    }

    override fun startPlayer() {
        playerRepository.startPlayer()
    }

    override fun pausePlayer() {
        playerRepository.pausePlayer()
    }

    override fun getCurrentPlayTime(): String {
        return playerRepository.getCurrentPlayTime()
    }

    override fun releasePlayer() {
        playerRepository.releasePlayer()
    }
}
