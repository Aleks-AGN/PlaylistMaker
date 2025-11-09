package com.aleksagn.playlistmaker.domain.impl

import com.aleksagn.playlistmaker.domain.api.SearchHistoryInteractor
import com.aleksagn.playlistmaker.domain.api.SearchHistoryRepository
import com.aleksagn.playlistmaker.domain.models.Track
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class SearchHistoryInteractorImpl(
    private val repository: SearchHistoryRepository
) : SearchHistoryInteractor {

    override fun saveTrackToHistory(track: Track) {
        repository.saveTrackToHistory(track)
    }

    override fun getTracksHistory(): Flow<List<Track>> {
        return repository.getTracksHistory().map { result ->
            result.data!!
        }
    }

    override fun clearTracksHistory() {
        repository.clearTracksHistory()
    }
}
