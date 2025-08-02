package com.aleksagn.playlistmaker.data.impl

import com.aleksagn.playlistmaker.data.StorageClient
import com.aleksagn.playlistmaker.domain.api.SearchHistoryRepository
import com.aleksagn.playlistmaker.domain.models.Track
import com.aleksagn.playlistmaker.util.Resource

class SearchHistoryRepositoryImpl(
    private val storage: StorageClient<ArrayList<Track>>): SearchHistoryRepository {

    override fun saveTrackToHistory(track: Track) {
        val tracks = storage.getData() ?: arrayListOf()

        if (tracks.isEmpty()) {
            tracks.add(track)
        } else {
            val index = tracks.indexOfFirst { it.trackId == track.trackId }
            if (index == -1) {
                if (tracks.size == 10) {
                    tracks.removeAt(9)
                }
                tracks.add(0, track)
            } else {
                tracks.removeAt(index)
                tracks.add(0, track)
            }
        }
        storage.storeData(tracks)
    }

    override fun getTracksHistory(): Resource<List<Track>> {
        val tracks = storage.getData() ?: listOf()
        return Resource.Success(tracks)
    }

    override fun clearTracksHistory() {
        storage.clearData()
    }
}
