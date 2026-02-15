package com.aleksagn.playlistmaker.ui.library

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.ui.platform.ComposeView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.aleksagn.playlistmaker.R
import com.aleksagn.playlistmaker.databinding.FragmentLibraryBinding
import com.aleksagn.playlistmaker.presentation.library.FavoritesViewModel
import com.aleksagn.playlistmaker.presentation.library.PlaylistsViewModel
import com.aleksagn.playlistmaker.ui.player.PlayerFragment
import com.aleksagn.playlistmaker.ui.search.SearchScreen
import com.google.android.material.tabs.TabLayoutMediator
import com.google.gson.Gson
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel
import kotlin.getValue

class LibraryFragment  : Fragment() {

//    private var _binding: FragmentLibraryBinding? = null
//    private val binding get() = _binding!!
//
//    private lateinit var tabMediator: TabLayoutMediator

    private val favoritesViewModel: FavoritesViewModel by viewModel()
    private val playlistsViewModel: PlaylistsViewModel by viewModel()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
//        _binding = FragmentLibraryBinding.inflate(inflater, container, false)
//        return binding.root
        return ComposeView(requireContext()).apply {
            setContent {
                LibraryScreen(
                    favoritesViewModel = favoritesViewModel,
                    playlistsViewModel = playlistsViewModel,
                    navigateToPlayer = { track ->
                        val json: Gson by inject()
                        val jsonTrack = json.toJson(track)

                        findNavController().navigate(R.id.action_libraryFragment_to_playerFragment,
                            PlayerFragment.createArgs(jsonTrack))
                    },
                    navigateToViewer = { playlistId ->
                        findNavController().navigate(R.id.action_libraryFragment_to_playlistViewerFragment,
                            PlaylistViewerFragment.createArgs(playlistId))
                    },
                    onNewPlaylistButtonClick = {
                        findNavController().navigate(R.id.action_libraryFragment_to_playlistCreatorFragment,
                            PlaylistCreatorFragment.createArgs(playlistId = -1))
                    }
                )
            }
        }
    }

//    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
//        super.onViewCreated(view, savedInstanceState)
//
//        binding.viewPager.adapter = LibraryViewPagerAdapter(
//            fragmentManager = childFragmentManager,
//            lifecycle = lifecycle
//        )
//
//        tabMediator = TabLayoutMediator(binding.tabLayout, binding.viewPager) { tab, position ->
//            when(position) {
//                0 -> tab.text = getString(R.string.favorites)
//                1 -> tab.text = getString(R.string.playlists)
//            }
//        }
//        tabMediator.attach()
//
//    }
//
//    override fun onDestroyView() {
//        super.onDestroyView()
//        tabMediator.detach()
//        _binding = null
//    }
    override fun onResume() {
        super.onResume()
        favoritesViewModel.getFavoriteTracks()
        playlistsViewModel.getPlaylists()
    }
}
