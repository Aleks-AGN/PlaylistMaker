package com.aleksagn.playlistmaker.domain.impl

import com.aleksagn.playlistmaker.domain.api.FavoriteTracksInteractor
import com.aleksagn.playlistmaker.domain.api.FavoriteTracksRepository
import com.aleksagn.playlistmaker.domain.models.Track
import kotlinx.coroutines.flow.Flow

class FavoriteTracksInteractorImpl(
    private val repository: FavoriteTracksRepository
) : FavoriteTracksInteractor {
    override suspend fun insertFavoriteTrack(track: Track) {
        repository.insertFavoriteTrack(track)
    }

    override suspend fun deleteFavoriteTrackById(trackId: Int) {
        repository.deleteFavoriteTrackById(trackId)
    }

    override fun getFavoriteTracks(): Flow<List<Track>> {
        return repository.getFavoriteTracks()
    }

    override fun observeFavoriteTrackById(trackId: Int): Flow<Boolean> {
        return repository.observeFavoriteTrackById(trackId)
    }
}
