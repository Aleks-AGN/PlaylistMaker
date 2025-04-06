package com.aleksagn.playlistmaker.data.impl

import android.media.MediaPlayer
import com.aleksagn.playlistmaker.domain.api.PlayerRepository
import java.text.SimpleDateFormat
import java.util.Locale

class PlayerRepositoryImpl : PlayerRepository {

    companion object {
        private const val STATE_DEFAULT = 0
        private const val STATE_PREPARED = 1
        private const val STATE_PLAYING = 2
        private const val STATE_PAUSED = 3
    }

    private var playerState = STATE_DEFAULT
    private var mediaPlayer = MediaPlayer()

    override fun preparePlayer(previewUrl: String, onPrepare: () -> Unit, onComplete: () -> Unit) {
        mediaPlayer.setDataSource(previewUrl)
        mediaPlayer.prepareAsync()

        mediaPlayer.setOnPreparedListener {
            onPrepare()
            playerState = STATE_PREPARED
        }

        mediaPlayer.setOnCompletionListener {
            onComplete()
            playerState = STATE_PREPARED
        }
    }

    override fun startPlayer() {
        mediaPlayer.start()
        playerState = STATE_PLAYING
    }

    override fun pausePlayer() {
        mediaPlayer.pause()
        playerState = STATE_PAUSED
    }

    override fun getCurrentPlayTime(): String {
        if (playerState == STATE_PLAYING) {
            return SimpleDateFormat(
                "mm:ss",
                Locale.getDefault()
            ).format(mediaPlayer.currentPosition)
        } else {
            return ""
        }
    }

    override fun releasePlayer() {
        mediaPlayer.release()
    }
}
