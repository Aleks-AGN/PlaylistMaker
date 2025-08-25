package com.aleksagn.playlistmaker.domain.api

import com.aleksagn.playlistmaker.domain.models.Track
import com.aleksagn.playlistmaker.util.Resource

interface SearchHistoryRepository {
    fun saveTrackToHistory(track: Track)
    fun getTracksHistory(): Resource<List<Track>>
    fun clearTracksHistory()
}