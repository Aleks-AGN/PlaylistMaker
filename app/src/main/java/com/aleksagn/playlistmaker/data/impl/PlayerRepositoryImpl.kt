package com.aleksagn.playlistmaker.data.impl

import android.media.MediaPlayer
import com.aleksagn.playlistmaker.domain.api.PlayerRepository
import java.text.SimpleDateFormat
import java.util.Locale

class PlayerRepositoryImpl : PlayerRepository {

    enum class PlayerState {
        DEFAULT,
        PREPARED,
        PLAYING,
        PAUSED
    }

    private var playerState = PlayerState.DEFAULT
    private var mediaPlayer = MediaPlayer()

    override fun preparePlayer(previewUrl: String, onPrepare: () -> Unit, onComplete: () -> Unit) {
        mediaPlayer.setDataSource(previewUrl)
        mediaPlayer.prepareAsync()

        mediaPlayer.setOnPreparedListener {
            onPrepare()
            playerState = PlayerState.PREPARED
        }

        mediaPlayer.setOnCompletionListener {
            onComplete()
            playerState = PlayerState.PREPARED
        }
    }

    override fun startPlayer() {
        mediaPlayer.start()
        playerState = PlayerState.PLAYING
    }

    override fun pausePlayer() {
        mediaPlayer.pause()
        playerState = PlayerState.PAUSED
    }

    override fun getCurrentPlayTime(): String {
        if (playerState == PlayerState.PLAYING) {
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
