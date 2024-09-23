package com.aleksagn.playlistmaker

import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class SearchActivity : AppCompatActivity() {

    companion object {
        private const val SEARCH_TEXT = "SEARCH_TEXT"
        private const val DEFAULT_SEARCH_TEXT = ""
        private const val ITUNES_BASE_URL = "https://itunes.apple.com/"
        private const val NOTHING_FOUND = "NOTHING_FOUND"
        private const val NET_ERROR = "NET_ERROR"
    }

    private val retrofit = Retrofit.Builder()
        .baseUrl(ITUNES_BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private val iTunesService = retrofit.create(ITunesApi::class.java)
    private val tracks = ArrayList<Track>()
    private val adapter = TrackAdapter(tracks)
    private var searchText: String = DEFAULT_SEARCH_TEXT

    private lateinit var searchField: EditText
    private lateinit var trackListView: RecyclerView
    private lateinit var clearButton: ImageView
    private lateinit var updateButton: Button
    private lateinit var toolbar: Toolbar
    private lateinit var placeholderNothingFound: LinearLayout
    private lateinit var placeholderNetError: LinearLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        searchField = findViewById(R.id.search_field)
        trackListView = findViewById(R.id.trackList)
        clearButton = findViewById(R.id.btn_clear)
        updateButton = findViewById(R.id.btn_update)
        toolbar = findViewById(R.id.search_toolbar)
        placeholderNothingFound = findViewById(R.id.placeholder_nothing_found)
        placeholderNetError = findViewById(R.id.placeholder_net_error)

        trackListView.adapter = adapter

        toolbar.setNavigationOnClickListener {
            finish()
        }

        clearButton.setOnClickListener {
            searchField.setText("")
            tracks.clear()
            updateVisability()
            adapter.notifyDataSetChanged()
            val inputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
            inputMethodManager?.hideSoftInputFromWindow(it.windowToken, 0)
        }

        updateButton.setOnClickListener {
            updateVisability()
            performSearch()
        }

        searchField.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                clearButton.isVisible = !s.isNullOrEmpty()
                searchText = s.toString()
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
    }

    private fun performSearch() {
        if (searchField.text.isNotEmpty()) {
            iTunesService.searchTrack(searchField.text.toString()).enqueue(object :
                Callback<TracksResponse> {
                override fun onResponse(call: Call<TracksResponse>,
                                        response: Response<TracksResponse>
                ) {
                    Log.d("SearchActivity", "onResponse: ${response.body()}")
                    if (response.code() == 200) {
                        tracks.clear()
                        if (response.body()?.results?.isNotEmpty() == true) {
                            tracks.addAll(response.body()?.results!!)
                            adapter.notifyDataSetChanged()
                        }
                        if (tracks.isEmpty()) {
                            showMessage(NOTHING_FOUND)
                        }
                    } else {
                        showMessage(NET_ERROR)
                    }
                }

                override fun onFailure(call: Call<TracksResponse>, t: Throwable) {
                    Log.d("SearchActivity", "onFailure: $t")
                    showMessage(NET_ERROR)
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
