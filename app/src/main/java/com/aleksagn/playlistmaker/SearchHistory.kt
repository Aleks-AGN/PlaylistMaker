package com.aleksagn.playlistmaker

import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.content.Intent
import android.os.Handler
import android.os.Looper
import com.google.gson.Gson

class SearchHistory(
    val context: Context
): OnTrackClickListener {

    companion object {
        private const val CLICK_DEBOUNCE_DELAY = 1000L
    }

    val sharedPreferences = context.getSharedPreferences(PLAYLIST_MAKER_PREFERENCES, MODE_PRIVATE)
    private val handler = Handler(Looper.getMainLooper())
    private var isClickAllowed = true

    override fun onTrackClick(track: Track, context: Context) {
        if (clickDebounce()) {
            val historyTracks = getHistoryTracks()

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
            putHistoryTracks(historyTracks)

            val playerIntent = Intent(context, PlayerActivity::class.java)

            val jsonTrack = Gson().toJson(track)
            playerIntent.putExtra("track", jsonTrack)

            context.startActivity(playerIntent)
        }
    }

    fun getHistoryTracks(): ArrayList<Track> {
        val json = sharedPreferences.getString(SEARCH_HISTORY_LIST_KEY, null) ?: return ArrayList<Track>()
        val arr = Gson().fromJson(json, Array<Track>::class.java)
        return ArrayList<Track>(arr.toMutableList())
    }

    private fun putHistoryTracks(tracks: ArrayList<Track>) {
        sharedPreferences.edit()
            .putString(SEARCH_HISTORY_LIST_KEY, Gson().toJson(tracks.toTypedArray()))
            .apply()
    }

    fun clearHistoryTracks() {
        sharedPreferences.edit()
            .remove(SEARCH_HISTORY_LIST_KEY)
            .apply()
    }

    private fun clickDebounce() : Boolean {
        val current = isClickAllowed
        if (isClickAllowed) {
            isClickAllowed = false
            handler.postDelayed({ isClickAllowed = true }, CLICK_DEBOUNCE_DELAY)
        }
        return current
    }
}
