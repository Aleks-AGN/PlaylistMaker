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
import com.aleksagn.playlistmaker.ui.player.view_model.PlayerViewModel
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners

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
            enablePlayButton(it != PlayerViewModel.STATE_DEFAULT)
            changeButtonView(it == PlayerViewModel.STATE_PLAYING)
        }

        viewModel.observeProgressTime().observe(this) {
            binding.currentTrackTime.text = it
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

    private fun enablePlayButton(isEnabled: Boolean) {
        binding.btnPlay.isEnabled = isEnabled
    }

    private fun changeButtonView(isPlaying: Boolean) {
        if (isPlaying) {
            binding.btnPlay.isVisible = false
            binding.btnPause.isVisible = true
        } else {
            binding.btnPlay.isVisible = true
            binding.btnPause.isVisible = false
        }
    }

    fun dpToPx(dp: Float, context: Context): Int {
        return TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            dp,
            context.resources.displayMetrics).toInt()
    }
}
