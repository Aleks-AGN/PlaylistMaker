package com.aleksagn.playlistmaker.domain.api

import com.aleksagn.playlistmaker.domain.models.Track
import com.aleksagn.playlistmaker.util.Resource
import kotlinx.coroutines.flow.Flow

interface SearchHistoryRepository {
    fun saveTrackToHistory(track: Track)
    fun getTracksHistory(): Flow<Resource<List<Track>>>
    fun clearTracksHistory()
}