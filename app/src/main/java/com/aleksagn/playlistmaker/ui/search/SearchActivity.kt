package com.aleksagn.playlistmaker.ui.search

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import com.aleksagn.playlistmaker.util.Creator
import com.aleksagn.playlistmaker.databinding.ActivitySearchBinding
import com.aleksagn.playlistmaker.domain.models.Track
import com.aleksagn.playlistmaker.domain.api.TracksInteractor
import com.aleksagn.playlistmaker.ui.player.PlayerActivity

class SearchActivity : AppCompatActivity() {

    companion object {
        private const val SEARCH_TEXT = "SEARCH_TEXT"
        private const val DEFAULT_SEARCH_TEXT = ""
        private const val NOTHING_FOUND = "NOTHING_FOUND"
        private const val NET_ERROR = "NET_ERROR"
        private const val SEARCH_DEBOUNCE_DELAY = 2000L
        private const val CLICK_DEBOUNCE_DELAY = 1000L
    }

    private val tracksInteractor = Creator.provideTracksInteractor()
    private val searchHistoryInteractor = Creator.provideSearchHistoryInteractor()
    private val handler = Handler(Looper.getMainLooper())
    private var isClickAllowed = true

    private val tracks = ArrayList<Track>()
    private var historyTracks = ArrayList<Track>()
    private val adapter = TrackAdapter(tracks)
    private val historyAdapter = TrackAdapter(historyTracks)
    private var searchText: String = DEFAULT_SEARCH_TEXT
    private val mainThreadHandler = Handler(Looper.getMainLooper())
    private val searchRunnable = Runnable { performSearch() }

    private lateinit var binding: ActivitySearchBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySearchBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val onTrackClickListener = object : TrackAdapter.OnTrackClickListener {
            override fun onTrackClick(track: Track) {
                if (clickDebounce()) {
                    searchHistoryInteractor.saveTrackToHistory(track)
                    val playerIntent = Intent(this@SearchActivity, PlayerActivity::class.java)
                    val jsonTrack = Creator.getGson().toJson(track)
                    playerIntent.putExtra("track", jsonTrack)
                    startActivity(playerIntent)
                }
            }
        }

        binding.trackList.adapter = adapter
        adapter.onTrackClickListener = onTrackClickListener

        historyTracks = searchHistoryInteractor.getTracksHistory().toCollection(ArrayList())
        historyAdapter.tracks = historyTracks

        binding.historyTrackList.adapter = historyAdapter
        historyAdapter.onTrackClickListener = onTrackClickListener

        binding.searchToolbar.setNavigationOnClickListener {
            finish()
        }

        binding.btnClear.setOnClickListener {
            binding.searchField.setText("")
            tracks.clear()
            updateVisability()
            adapter.notifyDataSetChanged()
            historyTracks = searchHistoryInteractor.getTracksHistory().toCollection(ArrayList())

            if (historyTracks.isNotEmpty()) {
                historyAdapter.tracks = historyTracks
                historyAdapter.notifyDataSetChanged()
                binding.searchViewGroup.isVisible = false
                binding.historySearchViewGroup.isVisible = true
            } else {
                binding.searchViewGroup.isVisible = true
                binding.historySearchViewGroup.isVisible = false
            }

            val inputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
            inputMethodManager?.hideSoftInputFromWindow(it.windowToken, 0)
        }

        binding.btnUpdate.setOnClickListener {
            updateVisability()
            performSearch()
        }

        binding.btnClearSearchHistory.setOnClickListener {
            searchHistoryInteractor.clearTracksHistory()
            historyTracks.clear()
            historyAdapter.notifyDataSetChanged()
            binding.historySearchViewGroup.isVisible = false
        }

        binding.searchField.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                binding.btnClear.isVisible = !s.isNullOrEmpty()
                searchText = s.toString()
                historyTracks = searchHistoryInteractor.getTracksHistory().toCollection(ArrayList())

                if (binding.searchField.hasFocus() && s?.isEmpty() == true && historyTracks.isNotEmpty()) {
                    historyAdapter.tracks = historyTracks
                    historyAdapter.notifyDataSetChanged()
                    binding.searchViewGroup.isVisible = false
                    binding.historySearchViewGroup.isVisible = true
                } else if (binding.searchField.hasFocus() && s?.isEmpty() == true) {
                    tracks.clear()
                    adapter.notifyDataSetChanged()
                    updateVisability()
                } else {
                    updateVisability()
                    binding.searchViewGroup.isVisible = true
                    binding.historySearchViewGroup.isVisible = false
                }
                searchDebounce()
            }
            override fun afterTextChanged(s: Editable?) {}
        })

        binding.searchField.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                performSearch()
                true
            }
            false
        }

        binding.searchField.setOnFocusChangeListener { _, hasFocus ->
            historyTracks = searchHistoryInteractor.getTracksHistory().toCollection(ArrayList())

            if (hasFocus && binding.searchField.text.isEmpty() && historyTracks.isNotEmpty()) {
                historyAdapter.tracks = historyTracks
                historyAdapter.notifyDataSetChanged()
                binding.searchViewGroup.isVisible = false
                binding.historySearchViewGroup.isVisible = true
            } else {
                binding.searchViewGroup.isVisible = true
                binding.historySearchViewGroup.isVisible = false
            }
        }
    }

    private fun clickDebounce() : Boolean {
        val current = isClickAllowed
        if (isClickAllowed) {
            isClickAllowed = false
            handler.postDelayed({ isClickAllowed = true }, CLICK_DEBOUNCE_DELAY)
        }
        return current
    }

    private fun searchDebounce() {
        mainThreadHandler.removeCallbacks(searchRunnable)
        mainThreadHandler.postDelayed(searchRunnable, SEARCH_DEBOUNCE_DELAY)
    }

    private fun performSearch() {
        if (binding.searchField.text.isNotEmpty()) {
            binding.searchViewGroup.isVisible = false
            binding.progressBar.isVisible = true

            tracksInteractor.searchTracks(
                binding.searchField.text.toString(),
                consumer = object : TracksInteractor.TracksConsumer {
                    override fun consume(foundTracks: List<Track>?, errorMessage: String?) {
                        mainThreadHandler.post {
                            binding.progressBar.isVisible = false
                            binding.searchViewGroup.isVisible = true
                            if (foundTracks!= null) {
                                tracks.clear()
                                if (foundTracks.isNotEmpty()) {
                                    tracks.addAll(foundTracks)
                                    adapter.notifyDataSetChanged()
                                } else {
                                    showMessage(NOTHING_FOUND)
                                }
                            }
                            if (errorMessage != null) {
                                showMessage(NET_ERROR)
                            }
                        }
                    }
                })
        }
    }

    private fun updateVisability() {
        binding.trackList.isVisible = true
        binding.placeholderNothingFound.isVisible = false
        binding.placeholderNetError.isVisible = false
    }

    private fun showMessage(type: String) {
        binding.trackList.isVisible = false
        when (type) {
            NOTHING_FOUND -> {
                binding.placeholderNetError.isVisible = false
                binding.placeholderNothingFound.isVisible = true }
            NET_ERROR -> {
                binding.placeholderNothingFound.isVisible = false
                binding.placeholderNetError.isVisible = true }
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(SEARCH_TEXT, searchText)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        binding.searchField.setText(savedInstanceState.getString(SEARCH_TEXT, DEFAULT_SEARCH_TEXT))
    }
}
