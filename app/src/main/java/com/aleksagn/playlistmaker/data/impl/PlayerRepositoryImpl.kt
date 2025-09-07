package com.aleksagn.playlistmaker.data.impl

import android.media.MediaPlayer
import com.aleksagn.playlistmaker.domain.api.PlayerRepository
import java.text.SimpleDateFormat
import java.util.Locale

class PlayerRepositoryImpl(private var mediaPlayer: MediaPlayer) : PlayerRepository {
//class PlayerRepositoryImpl() : PlayerRepository {

//    private var mediaPlayer = MediaPlayer()

    enum class PlayerState {
        DEFAULT,
        PREPARED,
        PLAYING,
        PAUSED
    }

    private var playerState = PlayerState.DEFAULT

//    override fun preparePlayer(previewUrl: String, onPrepare: () -> Unit, onComplete: () -> Unit) {
//        mediaPlayer.setDataSource(url)
//        mediaPlayer.prepareAsync()
//        mediaPlayer.setOnPreparedListener { onPrepare() }
//        mediaPlayer.setOnCompletionListener { onComplete() }
//    }
    override fun preparePlayer(previewUrl: String, onPrepare: () -> Unit, onComplete: () -> Unit) {
        releasePlayer()
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
            setOnErrorListener { _, _, _ -> release(); true }
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
