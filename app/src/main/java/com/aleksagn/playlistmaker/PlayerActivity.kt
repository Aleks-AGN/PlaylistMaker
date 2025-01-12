package com.aleksagn.playlistmaker

import android.content.Context
import android.media.MediaPlayer
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.TypedValue
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.isVisible
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.google.gson.Gson
import java.text.SimpleDateFormat
import java.util.Locale

class PlayerActivity : AppCompatActivity() {

    companion object {
        private const val STATE_DEFAULT = 0
        private const val STATE_PREPARED = 1
        private const val STATE_PLAYING = 2
        private const val STATE_PAUSED = 3
        private const val DELAY = 250L
    }

    private var playerState = STATE_DEFAULT

    private var mediaPlayer = MediaPlayer()
    private lateinit var playButton: ImageButton
    private lateinit var pauseButton: ImageButton
    private lateinit var currentTrackTime: TextView
    private var previewUrl: String = ""
    private var mainThreadHandler: Handler? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_player)

        val toolbar = findViewById<Toolbar>(R.id.player_toolbar)
        toolbar.setNavigationOnClickListener {
            finish()
        }

        val cover = findViewById<ImageView>(R.id.cover)

        val trackName = findViewById<TextView>(R.id.track_name)
        val artistName = findViewById<TextView>(R.id.artist_name)
        playButton = findViewById(R.id.btn_play)
        pauseButton = findViewById(R.id.btn_pause)
        currentTrackTime = findViewById(R.id.current_track_time)

        val trackTime = findViewById<TextView>(R.id.track_time)
        val collectionName = findViewById<TextView>(R.id.collection_name)
        val releaseDate = findViewById<TextView>(R.id.release_date)
        val primaryGenreName = findViewById<TextView>(R.id.primary_genre_name)
        val country = findViewById<TextView>(R.id.country)

        playButton.setOnClickListener {
            startPlayer()
        }

        pauseButton.setOnClickListener {
            pausePlayer()
        }

        val intent = intent
        val jsonTrack = intent.getStringExtra("track").toString()

        val track = Gson().fromJson(jsonTrack, Track::class.java)

        trackName.text = track.trackName
        artistName.text = track.artistName

        trackTime.text = track.getFormatTime()
        //currentTrackTime.text = trackTime.text

        if (track.collectionName.isNullOrEmpty()) {
            collectionName.isVisible = false
        } else {
            collectionName.text = track.collectionName
            collectionName.isVisible = true
        }

        releaseDate.text = track.releaseDate.substring(0, 4)

        primaryGenreName.text = track.primaryGenreName
        country.text = track.country

        previewUrl = track.previewUrl

        Glide.with(applicationContext)
            .load(track.getCoverArtwork())
            .placeholder(R.drawable.ic_track_placeholder)
            .fitCenter()
            .centerCrop()
            .transform(RoundedCorners(dpToPx(8f,this.applicationContext)))
            .into(cover)

        mainThreadHandler = Handler(Looper.getMainLooper())

        preparePlayer()
    }

    override fun onPause() {
        super.onPause()
        pausePlayer()
    }

    override fun onDestroy() {
        super.onDestroy()
        mainThreadHandler?.removeCallbacks(updateCurrentPlayTime())
        mediaPlayer.release()
    }

    private fun preparePlayer() {
        mediaPlayer.setDataSource(previewUrl)

        mediaPlayer.prepareAsync()

        mediaPlayer.setOnPreparedListener {
            playButton.isEnabled = true
            playerState = STATE_PREPARED
        }

        mediaPlayer.setOnCompletionListener {
            playButton.isVisible = true
            pauseButton.isVisible = false
            playerState = STATE_PREPARED
            mainThreadHandler?.removeCallbacks(updateCurrentPlayTime())
            currentTrackTime.text = "00:00"
        }
    }

    private fun startPlayer() {
        mediaPlayer.start()
        playButton.isVisible = false
        pauseButton.isVisible = true
        playerState = STATE_PLAYING
        mainThreadHandler?.post(updateCurrentPlayTime())
    }

    private fun pausePlayer() {
        mediaPlayer.pause()
        playButton.isVisible = true
        pauseButton.isVisible = false
        playerState = STATE_PAUSED
        mainThreadHandler?.removeCallbacks(updateCurrentPlayTime())
    }

    private fun updateCurrentPlayTime(): Runnable {
        return object : Runnable {
            override fun run() {
                if (playerState == STATE_PLAYING) {
                    currentTrackTime.text = SimpleDateFormat(
                        "mm:ss",
                        Locale.getDefault()
                    ).format(mediaPlayer.currentPosition)
                    mainThreadHandler?.postDelayed(this, DELAY)
                }
            }
        }
    }

    fun dpToPx(dp: Float, context: Context): Int {
        return TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            dp,
            context.resources.displayMetrics).toInt()
    }
}
