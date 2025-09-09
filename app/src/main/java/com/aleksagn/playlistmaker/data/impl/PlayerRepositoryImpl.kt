package com.aleksagn.playlistmaker.data.impl

import android.media.MediaPlayer
import com.aleksagn.playlistmaker.domain.api.PlayerRepository
import java.text.SimpleDateFormat
import java.util.Locale

class PlayerRepositoryImpl(private var mediaPlayer: MediaPlayer) : PlayerRepository {

    enum class PlayerState {
        DEFAULT,
        PREPARED,
        PLAYING,
        PAUSED
    }

    private var playerState = PlayerState.DEFAULT

    override fun preparePlayer(previewUrl: String, onPrepare: () -> Unit, onComplete: () -> Unit) {
        mediaPlayer.reset()
        mediaPlayer.stop()

        mediaPlayer = MediaPlayer().apply {
            setDataSource(previewUrl)
            prepareAsync()
            setOnPreparedListener {
                onPrepare()
                playerState = PlayerState.PREPARED
            }
            setOnCompletionListener {
                onComplete()
                playerState = PlayerState.PREPARED
            }
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
            return SimpleDateFormat("mm:ss", Locale.getDefault()).format(mediaPlayer.currentPosition)
        } else {
            return ""
        }
    }

    override fun releasePlayer() {
        mediaPlayer.release()
    }
}
