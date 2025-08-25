package com.aleksagn.playlistmaker.domain.impl

import com.aleksagn.playlistmaker.domain.api.SearchHistoryInteractor
import com.aleksagn.playlistmaker.domain.api.SearchHistoryRepository
import com.aleksagn.playlistmaker.domain.models.Track

class SearchHistoryInteractorImpl(
    private val repository: SearchHistoryRepository
) : SearchHistoryInteractor {

    override fun saveTrackToHistory(track: Track) {
        repository.saveTrackToHistory(track)
    }

    override fun getTracksHistory(): List<Track> {
        return repository.getTracksHistory().data!!
    }

    override fun clearTracksHistory() {
        repository.clearTracksHistory()
    }
}
