package com.aleksagn.playlistmaker.ui.library

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.aleksagn.playlistmaker.R
import com.aleksagn.playlistmaker.databinding.FragmentPlaylistsBinding
import com.aleksagn.playlistmaker.domain.models.Playlist
import com.aleksagn.playlistmaker.presentation.library.PlaylistsState
import com.aleksagn.playlistmaker.presentation.library.PlaylistsViewModel
import com.aleksagn.playlistmaker.util.debounce
import org.koin.androidx.viewmodel.ext.android.viewModel

class PlaylistsFragment : Fragment() {

    companion object {
        private const val CLICK_DEBOUNCE_DELAY = 300L

        fun newInstance() : PlaylistsFragment {
            return PlaylistsFragment()
        }
    }

    private lateinit var onPlaylistClickDebounce: (Playlist) -> Unit

    private val playlistsViewModel: PlaylistsViewModel by viewModel()

    private var _binding: FragmentPlaylistsBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        _binding = FragmentPlaylistsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        playlistsViewModel.getPlaylists()

        playlistsViewModel.observeState().observe(viewLifecycleOwner) {
            render(it)
        }

        binding.btnNewPlaylist.setOnClickListener {
            requireParentFragment()
                .findNavController()
                .navigate(R.id.action_libraryFragment_to_playlistCreatorFragment,
                PlaylistCreatorFragment.createArgs(playlistId = -1))
        }

        binding.playlistsList.layoutManager = GridLayoutManager(requireContext(), 2)

        onPlaylistClickDebounce = debounce<Playlist>(PlaylistsFragment.CLICK_DEBOUNCE_DELAY, viewLifecycleOwner.lifecycleScope,false) { playlist ->
            requireParentFragment()
                .findNavController()
                .navigate(R.id.action_libraryFragment_to_playlistViewerFragment,
                    PlaylistViewerFragment.createArgs(playlist.playlistId))
        }
    }

    fun showContent(playlists: List<Playlist>) {
        binding.playlistsList.isVisible = true
        binding.placeholderNothingFound.isVisible = false
        binding.playlistsList.adapter = PlaylistsAdapter(playlists, object :
            PlaylistsAdapter.OnPlaylistItemClickListener {
            override fun onItemClick(position: Int) {
                onPlaylistClickDebounce(playlists[position])
            }
        })
    }

    fun showEmpty() {
        binding.playlistsList.isVisible = false
        binding.placeholderNothingFound.isVisible = true
    }

    fun render(state: PlaylistsState) {
        when (state) {
            is PlaylistsState.Content -> showContent(state.playlists)
            is PlaylistsState.Empty -> showEmpty()
        }
    }
}
