package com.aleksagn.playlistmaker.ui.library

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView.ScaleType.CENTER_CROP
import android.widget.LinearLayout
import androidx.core.content.res.ResourcesCompat
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.aleksagn.playlistmaker.R
import com.aleksagn.playlistmaker.databinding.FragmentPlaylistViewerBinding
import com.aleksagn.playlistmaker.domain.models.Playlist
import com.aleksagn.playlistmaker.domain.models.Track
import com.aleksagn.playlistmaker.presentation.library.PlaylistViewerState
import com.aleksagn.playlistmaker.presentation.library.PlaylistViewerViewModel
import com.aleksagn.playlistmaker.ui.player.PlayerFragment
import com.aleksagn.playlistmaker.util.debounce
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import com.google.gson.Gson
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel

class PlaylistViewerFragment : Fragment() {

    companion object {
        private const val ARGS_PLAYLIST_ID = "playlist_id"
        private const val CLICK_DEBOUNCE_DELAY = 300L

        fun createArgs(playlistId: Int): Bundle =
            bundleOf(ARGS_PLAYLIST_ID to playlistId)
    }

    private lateinit var onTrackClickDebounce: (Track) -> Unit
    private lateinit var bottomSheetBehavior: BottomSheetBehavior<LinearLayout>

    private val playlistViewerViewModel: PlaylistViewerViewModel by viewModel()
    private var playlistId: Int = 0

    private var _binding: FragmentPlaylistViewerBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        _binding = FragmentPlaylistViewerBinding.inflate(inflater, container, false)

        playlistId = requireArguments().getInt(ARGS_PLAYLIST_ID)
        playlistViewerViewModel.setPlaylist(playlistId)
        bottomSheetBehavior = BottomSheetBehavior.from(binding.bottomSheet)

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        onTrackClickDebounce = debounce<Track>(PlaylistViewerFragment.CLICK_DEBOUNCE_DELAY, viewLifecycleOwner.lifecycleScope, false) { track ->

            val json: Gson by inject()
            val jsonTrack = json.toJson(track)

            findNavController().navigate(
                R.id.action_playlistViewerFragment_to_playerFragment,
                PlayerFragment.createArgs(jsonTrack))
        }

        playlistViewerViewModel.observeState().observe(viewLifecycleOwner) {
            render(it)
        }

        bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
        binding.menu.setOnClickListener {
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_HALF_EXPANDED
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

        binding.share.setOnClickListener {
            sharePlaylist(it)
        }

        binding.playerToolbar.setNavigationOnClickListener {
            findNavController().navigateUp()
        }
    }

    private fun setTrackList(tracks: List<Track>) {
        binding.tracksList.adapter = PlaylistTracksAdapter(tracks,
            object : PlaylistTracksAdapter.OnTrackItemClickListener {
                override fun onTrackItemClick(position: Int) {
                    onTrackClickDebounce(tracks[position])
                }
            },
            object : PlaylistTracksAdapter.OnTrackItemLongClickListener {
                override fun onTrackItemLongClick(position: Int) {
                    deleteTrackDialog(tracks[position].trackId, playlistId, position)
                }
            }
        )
        binding.tracksList.layoutManager = LinearLayoutManager(requireContext())
    }

    private fun updateBottomSheet() {
        val playlist: Playlist = playlistViewerViewModel.getPlaylist()
        if (playlist.playlistImageUri != null && playlist.playlistImageUri.toString().isNotEmpty()) {
            binding.image.setImageURI(playlist.playlistImageUri)
            binding.image.setScaleType(CENTER_CROP)
        }
        else {
            binding.image.setImageDrawable(
                ResourcesCompat.getDrawable(
                    requireContext().resources,
                    R.drawable.ic_playlist_placeholder, null
                )
            )
        }
        binding.title.setText(playlist.playlistTitle)
        binding.count.setText(playlistViewerViewModel.getTracksAmountString())

        binding.sharing.setOnClickListener {
            sharePlaylist(it)
        }
        binding.editInformation.setOnClickListener {
            editPlaylist(it)
        }
        binding.deletePlaylist.setOnClickListener {
            deletePlaylistDialog()
        }
    }

    private fun updatePlaylistData() {
        val playlist: Playlist = playlistViewerViewModel.getPlaylist()
        binding.playlistName.setText(playlist.playlistTitle)
        binding.playlistDescription.setText(playlist.playlistDescription)
        binding.playlistTime.setText(playlistViewerViewModel.getPlaylistDuration())
        binding.playlistTracksAmount.setText(playlistViewerViewModel.getTracksAmountString())
        if (playlist.playlistImageUri != null && playlist.playlistImageUri.toString().isNotEmpty()) {
            binding.plImage.setImageURI(playlist.playlistImageUri)
            binding.plImage.setScaleType(CENTER_CROP)
        }
        else {
            binding.plImage.setImageDrawable(
                ResourcesCompat.getDrawable(
                    requireContext().resources,
                    R.drawable.ic_playlist_viewer_placeholder, null
                )
            )
        }
    }

    private fun render(state: PlaylistViewerState) {
        when (state) {
            is PlaylistViewerState.Content -> {
                binding.noTracks.isVisible = false
                binding.tracksList.isVisible = true
                setTrackList(state.tracks)
            }
            else -> {
                binding.noTracks.isVisible = true
                binding.tracksList.isVisible = false
            }
        }
        updatePlaylistData()
        updateBottomSheet()
    }

    private  fun deleteTrackDialog(trackId:Int, playlistId:Int, position:Int) {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle(requireContext().getString(R.string.delete_track_dialog_message))
            .setNeutralButton(requireContext().getString(R.string.no)) { dialog, which ->
            }
            .setPositiveButton(requireContext().getString(R.string.yes)) { dialog, which ->
                playlistViewerViewModel.deleteTrack(trackId, playlistId)
                binding.tracksList.adapter?.notifyItemRemoved(position)
            }
            .show()
    }

    private  fun deletePlaylistDialog() {
        bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
        MaterialAlertDialogBuilder(requireContext())
            .setTitle(requireContext().getString(R.string.delete_playlist_dialog_title))
            .setMessage(requireContext().getString(R.string.delete_playlist_dialog_message))
            .setNeutralButton(requireContext().getString(R.string.no)) { dialog, which ->
            }
            .setPositiveButton(requireContext().getString(R.string.yes)) { dialog, which ->
                playlistViewerViewModel.deletePlaylist()
                findNavController().navigateUp()
            }
            .show()
    }

    private fun sharePlaylist(view: View) {
        bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN

        val message = playlistViewerViewModel.makeSharingPlaylistMessage()

        when {
            message.isNotEmpty() -> playlistViewerViewModel.sharePlaylist(message)
            else -> Snackbar.make(view, requireContext().getString(R.string.no_tracks),
                Snackbar.LENGTH_LONG).show()
        }
    }

    private fun editPlaylist(view: View) {
        findNavController().navigate(
            R.id.action_playlistViewerFragment_to_playlistCreatorFragment,
            PlaylistCreatorFragment.createArgs(playlistId = playlistId))
    }
}
