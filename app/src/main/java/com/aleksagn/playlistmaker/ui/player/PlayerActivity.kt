package com.aleksagn.playlistmaker.ui.player

import android.content.Context
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
import com.aleksagn.playlistmaker.R
import com.aleksagn.playlistmaker.creator.Creator
import com.aleksagn.playlistmaker.domain.models.Track
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import java.text.SimpleDateFormat
import java.util.Locale

class PlayerActivity : AppCompatActivity() {

    companion object {
        private const val DELAY = 250L
    }

    private lateinit var playButton: ImageButton
    private lateinit var pauseButton: ImageButton
    private lateinit var currentTrackTime: TextView
    private var previewUrl: String = ""
    private var mainThreadHandler: Handler? = null
    private val playerInteractor = Creator.providePlayerInteractor()

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
        val trackTime = findViewById<TextView>(R.id.track_time)
        val collectionName = findViewById<TextView>(R.id.collection_name)
        val releaseDate = findViewById<TextView>(R.id.release_date)
        val primaryGenreName = findViewById<TextView>(R.id.primary_genre_name)
        val country = findViewById<TextView>(R.id.country)
        currentTrackTime = findViewById(R.id.current_track_time)

        playButton = findViewById(R.id.btn_play)
        playButton.setOnClickListener {
            startPlayer()
        }

        pauseButton = findViewById(R.id.btn_pause)
        pauseButton.setOnClickListener {
            pausePlayer()
        }

        val intent = intent
        val jsonTrack = intent.getStringExtra("track").toString()
        val track = Creator.getGson().fromJson(jsonTrack, Track::class.java)

        trackName.text = track.trackName
        artistName.text = track.artistName
        trackTime.text = track.getFormatTime()

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
        playerInteractor.releasePlayer()
    }

    private fun preparePlayer() {
        val onPrepare = {
            playButton.isEnabled = true
        }

        val onComplete = {
            playButton.isVisible = true
            pauseButton.isVisible = false
            mainThreadHandler?.removeCallbacks(updateCurrentPlayTime())
            currentTrackTime.text = SimpleDateFormat("mm:ss", Locale.getDefault()).format(0)
        }

        playerInteractor.preparePlayer(previewUrl, onPrepare, onComplete)
    }

    private fun startPlayer() {
        playerInteractor.startPlayer()
        playButton.isVisible = false
        pauseButton.isVisible = true
        mainThreadHandler?.post(updateCurrentPlayTime())
    }

    private fun pausePlayer() {
        playerInteractor.pausePlayer()
        playButton.isVisible = true
        pauseButton.isVisible = false
        mainThreadHandler?.removeCallbacks(updateCurrentPlayTime())
    }

    private fun updateCurrentPlayTime(): Runnable {
        return object : Runnable {
            override fun run() {
                val time = playerInteractor.getCurrentPlayTime()

                if (time.isNotEmpty()) {
                    currentTrackTime.text = time
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
