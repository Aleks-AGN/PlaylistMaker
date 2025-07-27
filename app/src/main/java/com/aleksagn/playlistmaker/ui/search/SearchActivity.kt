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
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ProgressBar
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.aleksagn.playlistmaker.R
import com.aleksagn.playlistmaker.creator.Creator
import com.aleksagn.playlistmaker.domain.models.Track
import com.aleksagn.playlistmaker.domain.api.TracksInteractor
import com.aleksagn.playlistmaker.domain.models.TracksResponse
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
    private val historyTracksRepository = Creator.getHistoryTracksRepository()
    private val handler = Handler(Looper.getMainLooper())
    private var isClickAllowed = true

    private val tracks = ArrayList<Track>()
    private var historyTracks = ArrayList<Track>()
    private val adapter = TrackAdapter(tracks)
    private val historyAdapter = TrackAdapter(historyTracks)
    private var searchText: String = DEFAULT_SEARCH_TEXT
    private val mainThreadHandler = Handler(Looper.getMainLooper())
    private val searchRunnable = Runnable { performSearch() }

    private lateinit var toolbar: Toolbar
    private lateinit var searchField: EditText
    private lateinit var clearButton: ImageView

    private lateinit var progressBar: ProgressBar

    private lateinit var searchViewGroup: LinearLayout
    private lateinit var trackListView: RecyclerView
    private lateinit var placeholderNothingFound: LinearLayout
    private lateinit var placeholderNetError: LinearLayout
    private lateinit var updateButton: Button

    private lateinit var historySearchViewGroup: LinearLayout
    private lateinit var historyTrackListView: RecyclerView
    private lateinit var clearSearchHistoryButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        toolbar = findViewById(R.id.search_toolbar)
        searchField = findViewById(R.id.search_field)
        clearButton = findViewById(R.id.btn_clear)

        progressBar = findViewById(R.id.progress_bar)

        searchViewGroup = findViewById(R.id.search_view_group)
        trackListView = findViewById(R.id.track_list)
        placeholderNothingFound = findViewById(R.id.placeholder_nothing_found)
        placeholderNetError = findViewById(R.id.placeholder_net_error)
        updateButton = findViewById(R.id.btn_update)

        historySearchViewGroup = findViewById(R.id.history_search_view_group)
        historyTrackListView = findViewById(R.id.history_track_list)
        clearSearchHistoryButton = findViewById(R.id.btn_clear_search_history)

        val onTrackClickListener = object : TrackAdapter.OnTrackClickListener {
            override fun onTrackClick(track: Track) {
                if (clickDebounce()) {
                    val historyTracks = historyTracksRepository.getHistoryTracks()
                    if (historyTracks.isEmpty()) {
                        historyTracks.add(track)
                    } else {
                        val index = historyTracks.indexOfFirst { it.trackId == track.trackId }
                        if (index == -1) {
                            if (historyTracks.size == 10) {
                                historyTracks.removeAt(9)
                            }
                            historyTracks.add(0, track)
                        } else {
                            historyTracks.removeAt(index)
                            historyTracks.add(0, track)
                        }
                    }
                    historyTracksRepository.putHistoryTracks(historyTracks)

                    val playerIntent = Intent(this@SearchActivity, PlayerActivity::class.java)

                    val jsonTrack = Creator.getGson().toJson(track)
                    playerIntent.putExtra("track", jsonTrack)

                    startActivity(playerIntent)
                }
            }
        }

        trackListView.adapter = adapter
        adapter.onTrackClickListener = onTrackClickListener

        historyTracks = historyTracksRepository.getHistoryTracks()
        historyAdapter.tracks = historyTracks

        historyTrackListView.adapter = historyAdapter
        historyAdapter.onTrackClickListener = onTrackClickListener

        toolbar.setNavigationOnClickListener {
            finish()
        }

        clearButton.setOnClickListener {
            searchField.setText("")
            tracks.clear()
            updateVisability()
            adapter.notifyDataSetChanged()
            historyTracks = historyTracksRepository.getHistoryTracks()

            if (historyTracks.isNotEmpty()) {
                historyAdapter.tracks = historyTracks
                historyAdapter.notifyDataSetChanged()
                searchViewGroup.isVisible = false
                historySearchViewGroup.isVisible = true
            } else {
                searchViewGroup.isVisible = true
                historySearchViewGroup.isVisible = false
            }

            val inputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
            inputMethodManager?.hideSoftInputFromWindow(it.windowToken, 0)
        }

        updateButton.setOnClickListener {
            updateVisability()
            performSearch()
        }

        clearSearchHistoryButton.setOnClickListener {
            historyTracksRepository.clearHistoryTracks()
            historyTracks.clear()
            historyAdapter.notifyDataSetChanged()
            historySearchViewGroup.isVisible = false
        }

        searchField.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                clearButton.isVisible = !s.isNullOrEmpty()
                searchText = s.toString()
                historyTracks = historyTracksRepository.getHistoryTracks()

                if (searchField.hasFocus() && s?.isEmpty() == true && historyTracks.isNotEmpty()) {
                    historyAdapter.tracks = historyTracks
                    historyAdapter.notifyDataSetChanged()
                    searchViewGroup.isVisible = false
                    historySearchViewGroup.isVisible = true
                } else if (searchField.hasFocus() && s?.isEmpty() == true) {
                    tracks.clear()
                    adapter.notifyDataSetChanged()
                    updateVisability()
                } else {
                    updateVisability()
                    searchViewGroup.isVisible = true
                    historySearchViewGroup.isVisible = false
                }
                searchDebounce()
            }
            override fun afterTextChanged(s: Editable?) {}
        })

        searchField.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                performSearch()
                true
            }
            false
        }

        searchField.setOnFocusChangeListener { _, hasFocus ->
            historyTracks = historyTracksRepository.getHistoryTracks()

            if (hasFocus && searchField.text.isEmpty() && historyTracks.isNotEmpty()) {
                historyAdapter.tracks = historyTracks
                historyAdapter.notifyDataSetChanged()
                searchViewGroup.isVisible = false
                historySearchViewGroup.isVisible = true
            } else {
                searchViewGroup.isVisible = true
                historySearchViewGroup.isVisible = false
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
        if (searchField.text.isNotEmpty()) {
            searchViewGroup.isVisible = false
            progressBar.isVisible = true

            tracksInteractor.searchTracks(
                searchField.text.toString(),
                consumer = object : TracksInteractor.TracksConsumer {
                    override fun consume(foundTracks: TracksResponse) {
                        mainThreadHandler.post {
                            progressBar.isVisible = false
                            searchViewGroup.isVisible = true
                            if (foundTracks.resultCode == 200) {
                                tracks.clear()
                                if (foundTracks.results.isNotEmpty()) {
                                    tracks.addAll(foundTracks.results)
                                    adapter.notifyDataSetChanged()
                                } else {
                                    showMessage(NOTHING_FOUND)
                                }
                            } else {
                                showMessage(NET_ERROR)
                            }
                        }
                    }
                })
        }
    }

    private fun updateVisability() {
        trackListView.isVisible = true
        placeholderNothingFound.isVisible = false
        placeholderNetError.isVisible = false
    }

    private fun showMessage(type: String) {
        trackListView.isVisible = false
        when (type) {
            NOTHING_FOUND -> {
                placeholderNetError.isVisible = false
                placeholderNothingFound.isVisible = true }
            NET_ERROR -> {
                placeholderNothingFound.isVisible = false
                placeholderNetError.isVisible = true }
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(SEARCH_TEXT, searchText)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        searchField.setText(savedInstanceState.getString(SEARCH_TEXT, DEFAULT_SEARCH_TEXT))
    }
}
