package com.aleksagn.playlistmaker.ui.player

import android.content.Context
import android.os.Bundle
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.aleksagn.playlistmaker.R
import com.aleksagn.playlistmaker.databinding.FragmentPlayerBinding
import com.aleksagn.playlistmaker.domain.models.Track
import com.aleksagn.playlistmaker.presentation.player.PlayerState
import com.aleksagn.playlistmaker.presentation.player.PlayerViewModel
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.google.gson.Gson
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.text.SimpleDateFormat
import java.util.Locale

class PlayerFragment : Fragment() {

    companion object {
        private const val ARGS_TRACK_ID = "track_id"

        fun createArgs(trackId: String): Bundle =
            bundleOf(ARGS_TRACK_ID to trackId)
    }

    private val viewModel: PlayerViewModel by viewModel()
    private val gson: Gson by inject()

    private var _binding: FragmentPlayerBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = FragmentPlayerBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val jsonTrack = requireArguments().getString(ARGS_TRACK_ID).toString()

        val track = gson.fromJson(jsonTrack, Track::class.java)

        viewModel.preparePlayer(track.previewUrl)

        viewModel.observePlayerState().observe(viewLifecycleOwner) {
            render(it)
        }

        binding.playerToolbar.setNavigationOnClickListener {
            findNavController().navigateUp()
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

        Glide.with(this@PlayerFragment)
            .load(track.getCoverArtwork())
            .placeholder(R.drawable.ic_track_placeholder)
            .fitCenter()
            .centerCrop()
            .transform(RoundedCorners(dpToPx(8f, requireContext())))
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
