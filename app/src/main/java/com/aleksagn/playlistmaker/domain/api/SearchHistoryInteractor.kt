package com.aleksagn.playlistmaker.domain.api

import com.aleksagn.playlistmaker.domain.models.Track

interface SearchHistoryInteractor {
    fun saveTrackToHistory(track: Track)
    fun getTracksHistory() : List<Track>
    fun clearTracksHistory()
}