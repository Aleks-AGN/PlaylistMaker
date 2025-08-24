package com.aleksagn.playlistmaker.ui.player.activity

import android.content.Context
import android.os.Bundle
import android.util.TypedValue
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.lifecycle.ViewModelProvider
import com.aleksagn.playlistmaker.R
import com.aleksagn.playlistmaker.util.Creator
import com.aleksagn.playlistmaker.databinding.ActivityPlayerBinding
import com.aleksagn.playlistmaker.domain.models.Track
import com.aleksagn.playlistmaker.ui.player.view_model.PlayerState
import com.aleksagn.playlistmaker.ui.player.view_model.PlayerViewModel
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import java.text.SimpleDateFormat
import java.util.Locale

class PlayerActivity : AppCompatActivity() {

    private lateinit var viewModel: PlayerViewModel

    private lateinit var binding: ActivityPlayerBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPlayerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val jsonTrack = intent.getStringExtra("track").toString()
        val track = Creator.getGson().fromJson(jsonTrack, Track::class.java)

        viewModel = ViewModelProvider(this, PlayerViewModel.getFactory(track.previewUrl))
            .get(PlayerViewModel::class.java)

        viewModel.observePlayerState().observe(this) {
            render(it)
        }

        binding.playerToolbar.setNavigationOnClickListener {
            finish()
        }

        binding.btnPlay.setOnClickListener {
            viewModel.onPlayButtonClicked()
        }

        binding.btnPause.setOnClickListener {
            viewModel.onPauseButtonClicked()
        }

        binding.currentTrackTime.text = SimpleDateFormat("mm:ss", Locale.getDefault()).format(0)
        binding.trackName.text = track.trackName
        binding.artistName.text = track.artistName
        binding.trackTime.text = track.getFormatTime()
        if (track.collectionName.isEmpty()) {
            binding.collectionName.isVisible = false
        } else {
            binding.collectionName.text = track.collectionName
            binding.collectionName.isVisible = true
        }
        binding.releaseDate.text = track.releaseDate.substring(0, 4)
        binding.primaryGenreName.text = track.primaryGenreName
        binding.country.text = track.country

        Glide.with(applicationContext)
            .load(track.getCoverArtwork())
            .placeholder(R.drawable.ic_track_placeholder)
            .fitCenter()
            .centerCrop()
            .transform(RoundedCorners(dpToPx(8f,this.applicationContext)))
            .into(binding.cover)
    }

    override fun onPause() {
        super.onPause()
        viewModel.onPause()
    }

    fun render(state: PlayerState) {
        when (state) {
            is PlayerState.PreparePlayer -> showPrepare()
            is PlayerState.StartPlayer -> showStart()
            is PlayerState.PausePlayer -> showPause()
            is PlayerState.PlayingPlayer -> showPlaying(state.progressTime)
            is PlayerState.CompletePlayer -> showComplete()
        }
    }
    private fun showPrepare() {
        binding.btnPlay.isEnabled = true
    }

    private fun showStart() {
        binding.btnPlay.isVisible = false
        binding.btnPause.isVisible = true
    }

    private fun showPause() {
        binding.btnPlay.isVisible = true
        binding.btnPause.isVisible = false
    }

    private fun showPlaying(progressTime: String) {
        binding.currentTrackTime.text = progressTime
    }
    private fun showComplete() {
        binding.btnPlay.isVisible = true
        binding.btnPause.isVisible = false
        binding.currentTrackTime.text = SimpleDateFormat("mm:ss", Locale.getDefault()).format(0)
    }

    fun dpToPx(dp: Float, context: Context): Int {
        return TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            dp,
            context.resources.displayMetrics).toInt()
    }
}
