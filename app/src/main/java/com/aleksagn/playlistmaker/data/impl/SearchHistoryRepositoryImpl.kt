package com.aleksagn.playlistmaker.data.impl

import com.aleksagn.playlistmaker.data.StorageClient
import com.aleksagn.playlistmaker.data.db.AppDatabase
import com.aleksagn.playlistmaker.domain.api.SearchHistoryRepository
import com.aleksagn.playlistmaker.domain.models.Track
import com.aleksagn.playlistmaker.util.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class SearchHistoryRepositoryImpl(
    private val storage: StorageClient<ArrayList<Track>>,
    private val appDatabase: AppDatabase
) : SearchHistoryRepository {

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

    override fun getTracksHistory(): Flow<Resource<List<Track>>> = flow {
        val tracks = storage.getData() ?: listOf()
        val favoriteTracksIds = appDatabase.trackDao().getFavoriteTracksIds()
        tracks.forEach {
            when { favoriteTracksIds.contains(it.trackId) -> it.isFavorite = true }
        }
        emit(Resource.Success(tracks))
    }

    override fun clearTracksHistory() {
        storage.clearData()
    }
}
