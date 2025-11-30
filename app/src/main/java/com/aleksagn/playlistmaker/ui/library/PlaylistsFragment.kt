package com.aleksagn.playlistmaker.ui.library

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.aleksagn.playlistmaker.R
import com.aleksagn.playlistmaker.databinding.FragmentPlaylistsBinding
import com.aleksagn.playlistmaker.domain.models.Playlist
import com.aleksagn.playlistmaker.domain.models.Track
import com.aleksagn.playlistmaker.presentation.library.FavoritesState
import com.aleksagn.playlistmaker.presentation.library.PlaylistsState
import com.aleksagn.playlistmaker.presentation.library.PlaylistsViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf

class PlaylistsFragment : Fragment() {

    companion object {
        fun newInstance() : PlaylistsFragment {
            return PlaylistsFragment()
        }
    }

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
                .navigate(R.id.action_libraryFragment_to_playlistCreatorFragment)
        }

        binding.playlistsList.layoutManager = GridLayoutManager(requireContext(), 2)
    }

//    override fun onResume() {
//        super.onResume()
//        playlistsViewModel.getPlaylists()
//    }

    fun showContent(playlists: List<Playlist>) {
        binding.playlistsList.isVisible = true
        binding.placeholderNothingFound.isVisible = false
        binding.playlistsList.adapter = PlaylistsAdapter(playlists)
    }

    fun showEmpty(emptyMessage: String) {
        binding.playlistsList.isVisible = false
        binding.placeholderNothingFound.isVisible = true
    }

    fun render(state: PlaylistsState) {
        when (state) {
            is PlaylistsState.Content -> showContent(state.playlists)
            is PlaylistsState.Empty -> showEmpty(state.message)
        }
    }
}
