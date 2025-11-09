package com.aleksagn.playlistmaker.domain.api

import com.aleksagn.playlistmaker.domain.models.Track
import kotlinx.coroutines.flow.Flow

interface FavoriteTracksRepository {
    suspend fun insertFavoriteTrack(track: Track)
    suspend fun deleteFavoriteTrackById(trackId: Int)
    fun getFavoriteTracks(): Flow<List<Track>>
    fun observeFavoriteTrackById(trackId: Int): Flow<Boolean>
}