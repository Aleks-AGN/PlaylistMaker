package com.aleksagn.playlistmaker.ui.player

import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.TypedValue
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import com.aleksagn.playlistmaker.R
import com.aleksagn.playlistmaker.creator.Creator
import com.aleksagn.playlistmaker.databinding.ActivityPlayerBinding
import com.aleksagn.playlistmaker.domain.models.Track
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import java.text.SimpleDateFormat
import java.util.Locale

class PlayerActivity : AppCompatActivity() {

    companion object {
        private const val DELAY = 250L
    }

    private var previewUrl: String = ""
    private var mainThreadHandler: Handler? = null
    private val playerInteractor = Creator.providePlayerInteractor()

    private lateinit var binding: ActivityPlayerBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPlayerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.playerToolbar.setNavigationOnClickListener {
            finish()
        }

        binding.btnPlay.setOnClickListener {
            startPlayer()
        }

        binding.btnPause.setOnClickListener {
            pausePlayer()
        }

        val intent = intent
        val jsonTrack = intent.getStringExtra("track").toString()
        val track = Creator.getGson().fromJson(jsonTrack, Track::class.java)

        binding.trackName.text = track.trackName
        binding.artistName.text = track.artistName
        binding.trackTime.text = track.getFormatTime()

        if (track.collectionName.isNullOrEmpty()) {
            binding.collectionName.isVisible = false
        } else {
            binding.collectionName.text = track.collectionName
            binding.collectionName.isVisible = true
        }

        binding.releaseDate.text = track.releaseDate.substring(0, 4)
        binding.primaryGenreName.text = track.primaryGenreName
        binding.country.text = track.country

        previewUrl = track.previewUrl

        Glide.with(applicationContext)
            .load(track.getCoverArtwork())
            .placeholder(R.drawable.ic_track_placeholder)
            .fitCenter()
            .centerCrop()
            .transform(RoundedCorners(dpToPx(8f,this.applicationContext)))
            .into(binding.cover)

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
            binding.btnPlay.isEnabled = true
        }

        val onComplete = {
            binding.btnPlay.isVisible = true
            binding.btnPause.isVisible = false
            mainThreadHandler?.removeCallbacks(updateCurrentPlayTime())
            binding.currentTrackTime.text = SimpleDateFormat("mm:ss", Locale.getDefault()).format(0)
        }

        playerInteractor.preparePlayer(previewUrl, onPrepare, onComplete)
    }

    private fun startPlayer() {
        playerInteractor.startPlayer()
        binding.btnPlay.isVisible = false
        binding.btnPause.isVisible = true
        mainThreadHandler?.post(updateCurrentPlayTime())
    }

    private fun pausePlayer() {
        playerInteractor.pausePlayer()
        binding.btnPlay.isVisible = true
        binding.btnPause.isVisible = false
        mainThreadHandler?.removeCallbacks(updateCurrentPlayTime())
    }

    private fun updateCurrentPlayTime(): Runnable {
        return object : Runnable {
            override fun run() {
                val time = playerInteractor.getCurrentPlayTime()

                if (time.isNotEmpty()) {
                    binding.currentTrackTime.text = time
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
