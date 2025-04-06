package com.aleksagn.playlistmaker.ui.search

import android.content.Context
import android.content.Intent
import android.os.Handler
import android.os.Looper
import com.aleksagn.playlistmaker.creator.Creator
import com.aleksagn.playlistmaker.domain.models.Track
import com.aleksagn.playlistmaker.ui.player.PlayerActivity

class SearchHistory(): OnTrackClickListener {

    companion object {
        private const val CLICK_DEBOUNCE_DELAY = 1000L
    }

    val historyTracksRepository = Creator.getHistoryTracksRepository()
    private val handler = Handler(Looper.getMainLooper())
    private var isClickAllowed = true

    override fun onTrackClick(track: Track, context: Context) {
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

            val playerIntent = Intent(context, PlayerActivity::class.java)

            val jsonTrack = Creator.getGson().toJson(track)
            playerIntent.putExtra("track", jsonTrack)

            context.startActivity(playerIntent)
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
}
