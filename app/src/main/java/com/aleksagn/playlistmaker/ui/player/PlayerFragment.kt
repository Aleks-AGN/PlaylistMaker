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
import com.aleksagn.playlistmaker.presentation.player.PlayerViewModel
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
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

        viewModel.preparePlayer(track)

        viewModel.observePlayerState().observe(viewLifecycleOwner) {
            binding.btnPlay.isEnabled = it.isPlayButtonEnabled
            binding.btnPlay.isVisible = it.isPlayButtonVisible
            binding.btnPause.isVisible = !it.isPlayButtonVisible
            binding.currentTrackTime.text = it.progress

        }

        viewModel.observeIsFavorite.observe(viewLifecycleOwner) { isFavorite ->
            if (isFavorite)
                binding.btnFavoriteBorder.setImageResource(R.drawable.ic_btn_favorite_border_active)
            else
                binding.btnFavoriteBorder.setImageResource(R.drawable.ic_btn_favorite_border_inactive)
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

        binding.btnFavoriteBorder.setOnClickListener {
            viewModel.onFavoriteButtonClicked()
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
            .transform(
                CenterCrop(),
                RoundedCorners(dpToPx(8f, requireContext()))
            )
            .into(binding.cover)
    }

    override fun onPause() {
        super.onPause()
        viewModel.onPause()
    }

    private fun dpToPx(dp: Float, context: Context): Int {
        return TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            dp,
            context.resources.displayMetrics).toInt()
    }
}
