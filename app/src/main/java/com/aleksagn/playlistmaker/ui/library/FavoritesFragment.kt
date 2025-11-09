package com.aleksagn.playlistmaker.ui.library

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.aleksagn.playlistmaker.R
import com.aleksagn.playlistmaker.databinding.FragmentFavoritesBinding
import com.aleksagn.playlistmaker.domain.models.Track
import com.aleksagn.playlistmaker.presentation.library.FavoritesState
import com.aleksagn.playlistmaker.presentation.library.FavoritesViewModel
import com.aleksagn.playlistmaker.ui.player.PlayerFragment
import com.aleksagn.playlistmaker.ui.search.TrackAdapter
import com.aleksagn.playlistmaker.util.debounce
import com.google.gson.Gson
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel

class FavoritesFragment : Fragment() {

    companion object {
        fun newInstance(): FavoritesFragment {
            return FavoritesFragment()
        }
        private const val CLICK_DEBOUNCE_DELAY = 300L
    }

    private lateinit var onTrackClickDebounce: (Track) -> Unit

    private val adapter = TrackAdapter()

    private val favoritesViewModel: FavoritesViewModel by viewModel()

    private var _binding: FragmentFavoritesBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        _binding = FragmentFavoritesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        favoritesViewModel.getFavoriteTracks()

        favoritesViewModel.observeState().observe(viewLifecycleOwner) {
            render(it)
        }

        onTrackClickDebounce = debounce<Track>(FavoritesFragment.CLICK_DEBOUNCE_DELAY, viewLifecycleOwner.lifecycleScope, false) { track ->

            val json: Gson by inject()
            val jsonTrack = json.toJson(track)

            findNavController().navigate(R.id.action_libraryFragment_to_playerFragment,
                PlayerFragment.createArgs(jsonTrack))
        }

        val onTrackClickListener = object : TrackAdapter.OnTrackClickListener {
            override fun onTrackClick(track: Track) {
                onTrackClickDebounce(track)
            }
        }

        binding.trackList.adapter = adapter
        adapter.onTrackClickListener = onTrackClickListener
    }

    fun showContent(tracksList: List<Track>) {
        binding.trackList.isVisible = true
        binding.placeholderNothingFound.isVisible = false

        adapter.tracks.clear()
        adapter.tracks.addAll(tracksList)
        adapter.notifyDataSetChanged()
    }

    fun showEmpty(emptyMessage: String) {
        binding.trackList.isVisible = false
        binding.placeholderNothingFound.isVisible = true
    }

    fun render(state: FavoritesState) {
        when (state) {
            is FavoritesState.Content -> showContent(state.tracks)
            is FavoritesState.Empty -> showEmpty(state.message)
        }
    }
}
