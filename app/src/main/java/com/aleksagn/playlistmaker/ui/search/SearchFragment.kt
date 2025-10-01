package com.aleksagn.playlistmaker.ui.search

import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.aleksagn.playlistmaker.R
import com.aleksagn.playlistmaker.databinding.FragmentSearchBinding
import com.aleksagn.playlistmaker.domain.models.Track
import com.aleksagn.playlistmaker.ui.player.PlayerFragment
import com.aleksagn.playlistmaker.presentation.search.SearchState
import com.aleksagn.playlistmaker.presentation.search.SearchViewModel
import com.google.gson.Gson
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel

class SearchFragment : Fragment() {

    companion object {
        private const val CLICK_DEBOUNCE_DELAY = 1000L
    }

    private val viewModel: SearchViewModel by viewModel()

    private var textWatcher: TextWatcher? = null

    private var isClickAllowed = true
    private val handler = Handler(Looper.getMainLooper())

    private val adapter = TrackAdapter()
    private val historyAdapter = TrackAdapter()

    private var _binding: FragmentSearchBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = FragmentSearchBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.observeState().observe(viewLifecycleOwner) {
            render(it)
        }

        viewModel.observeShowToast().observe(viewLifecycleOwner) {
            showToast(it)
        }

        val onTrackClickListener = object : TrackAdapter.OnTrackClickListener {
            override fun onTrackClick(track: Track) {
                if (clickDebounce()) {
                    viewModel.saveTrackToHistory(track)

                    val json: Gson by inject()
                    val jsonTrack = json.toJson(track)

                    findNavController().navigate(R.id.action_searchFragment_to_playerFragment,
                        PlayerFragment.createArgs(jsonTrack))
                }
            }
        }

        binding.trackList.adapter = adapter
        adapter.onTrackClickListener = onTrackClickListener

        binding.historyTrackList.adapter = historyAdapter
        historyAdapter.onTrackClickListener = onTrackClickListener

        binding.btnClear.setOnClickListener {
            binding.searchField.setText("")
            showContent(emptyList())

            if (historyAdapter.tracks.isNotEmpty()) {
                showHistory()
            }

            val inputMethodManager = requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
            inputMethodManager?.hideSoftInputFromWindow(it.windowToken, 0)
        }

        binding.btnClearSearchHistory.setOnClickListener {
            viewModel.clearTracksHistory()
            binding.historySearchViewGroup.isVisible = false
        }

        binding.btnUpdate.setOnClickListener {
            viewModel.searchQuick(binding.searchField.text.toString())
        }

        binding.searchField.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                viewModel.searchQuick(binding.searchField.text.toString())
                true
            }
            false
        }

        binding.searchField.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus && binding.searchField.text.isEmpty() && historyAdapter.tracks.isNotEmpty()) {
                showHistory()
            }
        }

        textWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) { }
            override fun afterTextChanged(s: Editable?) { }
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                binding.btnClear.isVisible = !s.isNullOrEmpty()

                if (s?.isEmpty() == true && historyAdapter.tracks.isNotEmpty()) {
                    showHistory()
                } else if (s?.isEmpty() == true) {
                    showContent(emptyList())
                }
                viewModel.searchDebounce(changedText = s?.toString() ?: "")
            }
        }
        textWatcher?.let { binding.searchField.addTextChangedListener(it) }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        textWatcher?.let { binding.searchField.removeTextChangedListener(it) }
        _binding = null
    }

    private fun clickDebounce() : Boolean {
        val current = isClickAllowed
        if (isClickAllowed) {
            isClickAllowed = false
            handler.postDelayed({ isClickAllowed = true }, CLICK_DEBOUNCE_DELAY)
        }
        return current
    }

    fun showHistory() {
        binding.searchViewGroup.isVisible = false
        binding.historySearchViewGroup.isVisible = true
        binding.progressBar.isVisible = false
    }

    fun showLoading() {
        binding.searchViewGroup.isVisible = false
        binding.historySearchViewGroup.isVisible = false
        binding.progressBar.isVisible = true
    }

    fun showContent(tracksList: List<Track>) {
        binding.searchViewGroup.isVisible = true
        binding.trackList.isVisible = true
        binding.placeholderNothingFound.isVisible = false
        binding.placeholderNetError.isVisible = false
        binding.historySearchViewGroup.isVisible = false
        binding.progressBar.isVisible = false

        adapter.tracks.clear()
        adapter.tracks.addAll(tracksList)
        adapter.notifyDataSetChanged()
    }

    fun showHistory(tracksList: List<Track>) {
        historyAdapter.tracks.clear()
        historyAdapter.tracks.addAll(tracksList)
        historyAdapter.notifyDataSetChanged()
    }

    fun showError(errorMessage: String) {
        binding.searchViewGroup.isVisible = true
        binding.trackList.isVisible = false
        binding.placeholderNothingFound.isVisible = false
        binding.placeholderNetError.isVisible = true
        binding.historySearchViewGroup.isVisible = false
        binding.progressBar.isVisible = false
    }

    fun showEmpty(emptyMessage: String) {
        binding.searchViewGroup.isVisible = true
        binding.trackList.isVisible = false
        binding.placeholderNothingFound.isVisible = true
        binding.placeholderNetError.isVisible = false
        binding.historySearchViewGroup.isVisible = false
        binding.progressBar.isVisible = false
    }

    fun render(state: SearchState) {
        when (state) {
            is SearchState.Loading -> showLoading()
            is SearchState.Content -> showContent(state.tracks)
            is SearchState.History -> showHistory(state.tracks)
            is SearchState.Error -> showError(state.errorMessage)
            is SearchState.Empty -> showEmpty(state.message)
        }
    }

    fun showToast(message: String?) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_LONG).show()
    }
}
