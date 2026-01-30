package com.aleksagn.playlistmaker.ui.player

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.ServiceConnection
import android.os.Bundle
import android.os.IBinder
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.aleksagn.playlistmaker.R
import com.aleksagn.playlistmaker.databinding.FragmentPlayerBinding
import com.aleksagn.playlistmaker.domain.models.Playlist
import com.aleksagn.playlistmaker.domain.models.Track
import com.aleksagn.playlistmaker.presentation.library.PlaylistsState
import com.aleksagn.playlistmaker.presentation.player.PlayerState
import com.aleksagn.playlistmaker.presentation.player.PlayerViewModel
import com.aleksagn.playlistmaker.services.AudioPlayerService
import com.aleksagn.playlistmaker.ui.library.PlaylistCreatorFragment
import com.aleksagn.playlistmaker.util.ConnectivityBroadcastReceiver
import com.aleksagn.playlistmaker.util.debounce
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.snackbar.Snackbar
import com.google.gson.Gson
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.text.SimpleDateFormat
import java.util.Locale

class PlayerFragment : Fragment() {

    companion object {
        private const val ARGS_TRACK_ID = "track_id"
        private const val CLICK_DEBOUNCE_DELAY = 300L

        fun createArgs(trackId: String): Bundle =
            bundleOf(ARGS_TRACK_ID to trackId)
    }

    private lateinit var onPlaylistClickDebounce: (Playlist) -> Unit
    private lateinit var track: Track

    private val viewModel: PlayerViewModel by viewModel()
    private val gson: Gson by inject()

    private val connectivityBroadcastReceiver = ConnectivityBroadcastReceiver()

    private val serviceConnection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            val binder = service as AudioPlayerService.AudioPlayerServiceBinder
            viewModel.setAudioPlayerControl(binder.getService())
        }

        override fun onServiceDisconnected(name: ComponentName?) {
            viewModel.removeAudioPlayerControl()
        }
    }

    private var _binding: FragmentPlayerBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = FragmentPlayerBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onDestroyView() {
        unbindAudioPlayerService()
        super.onDestroyView()
        _binding = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val jsonTrack = requireArguments().getString(ARGS_TRACK_ID).toString()

        track = gson.fromJson(jsonTrack, Track::class.java)

        bindAudioPlayerService()

        viewModel.preparePlayer(track)

        viewModel.observePlayerState().observe(viewLifecycleOwner) {
            binding.btnPlay.isEnabled = it.isPlayButtonEnabled
            binding.btnPlay.isPlaying = it is PlayerState.Playing
            binding.currentTrackTime.text = it.progress
        }

        viewModel.observeIsFavorite.observe(viewLifecycleOwner) { isFavorite ->
            if (isFavorite)
                binding.btnFavoriteBorder.setImageResource(R.drawable.ic_btn_favorite_border_active)
            else
                binding.btnFavoriteBorder.setImageResource(R.drawable.ic_btn_favorite_border_inactive)
        }

        viewModel.observePlaylistState().observe(viewLifecycleOwner) {
            render(it)
        }

        binding.playerToolbar.setNavigationOnClickListener {
            findNavController().navigateUp()
        }

        binding.btnPlay.setOnClickListener {
            viewModel.onPlayButtonClicked()
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

        val bottomSheetBehavior = BottomSheetBehavior.from(binding.playlistsBottomSheet).apply {
            state = BottomSheetBehavior.STATE_HIDDEN
        }

        bottomSheetBehavior.addBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {
            override fun onStateChanged(bottomSheet: View, newState: Int) {
                when (newState) {
                    BottomSheetBehavior.STATE_HIDDEN -> {
                        binding.overlay.isVisible = false
                    } else -> {
                        binding.overlay.isVisible = true
                    }
                }
            }
            override fun onSlide(bottomSheet: View, slideOffset: Float) = Unit
        })

        binding.btnQueue.setOnClickListener {
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_HALF_EXPANDED
            viewModel.getPlaylists()
        }

        binding.btnNewPlaylist.setOnClickListener {
            findNavController().navigate(R.id.playerFragment_to_playlistCreatorFragment,
                PlaylistCreatorFragment.createArgs(playlistId = -1))
        }

        binding.playlistsList.layoutManager = LinearLayoutManager(requireContext())

        onPlaylistClickDebounce = debounce<Playlist>(CLICK_DEBOUNCE_DELAY, viewLifecycleOwner.lifecycleScope,false) { playlist ->
            val isTrackInPlaylist = viewModel.observeTrackInPlaylist(track, playlist)

            binding.playlistsList.let {
                if (isTrackInPlaylist) {
                    val message = requireContext().getString(R.string.track_already_added) + " " + playlist.playlistTitle
                    Snackbar.make(it, message, Snackbar.LENGTH_LONG).show()
                } else {
                    viewModel.addTrackToPlaylist(track, playlist)
                    val message = requireContext().getString(R.string.added_to_playlist) + " " + playlist.playlistTitle
                    Snackbar.make(it, message, Snackbar.LENGTH_LONG).show()
                    bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        viewModel.hideNotification()
        ContextCompat.registerReceiver(
            requireContext(),
            connectivityBroadcastReceiver,
            IntentFilter(ConnectivityBroadcastReceiver.ACTION),
            ContextCompat.RECEIVER_NOT_EXPORTED
        )
    }

    override fun onPause() {
        super.onPause()
        viewModel.showNotification()
        requireContext().unregisterReceiver(connectivityBroadcastReceiver)
    }

    private fun bindAudioPlayerService() {
        val intent = Intent(requireContext(), AudioPlayerService::class.java).apply {
            putExtra("preview_url", track.previewUrl)
            putExtra("artist_name", track.artistName)
            putExtra("track_name", track.trackName)
        }

        requireContext().bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE)
    }

    private fun unbindAudioPlayerService() {
        requireContext().unbindService(serviceConnection)
    }

    fun showContent(playlists: List<Playlist>) {
        binding.playlistsList.isVisible = true
        binding.playlistsList.adapter = PlayerPlaylistAdapter(playlists, object :
            PlayerPlaylistAdapter.OnPlaylistItemClickListener {
            override fun onItemClick(position: Int) {
                onPlaylistClickDebounce(playlists[position])
            }
        })
    }

    fun showEmpty() {
        binding.playlistsList.isVisible = false
    }

    fun render(state: PlaylistsState) {
        when (state) {
            is PlaylistsState.Content -> showContent(state.playlists)
            is PlaylistsState.Empty -> showEmpty()
        }
    }

    private fun dpToPx(dp: Float, context: Context): Int {
        return TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            dp,
            context.resources.displayMetrics).toInt()
    }
}
