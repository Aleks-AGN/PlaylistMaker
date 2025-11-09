package com.aleksagn.playlistmaker.domain.api

import com.aleksagn.playlistmaker.domain.models.Track
import kotlinx.coroutines.flow.Flow

interface SearchHistoryInteractor {
    fun saveTrackToHistory(track: Track)
    fun getTracksHistory() : Flow<List<Track>>
    fun clearTracksHistory()
}