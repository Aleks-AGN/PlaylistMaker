package com.aleksagn.playlistmaker.data.impl

import com.aleksagn.playlistmaker.data.converters.TrackDbConvertеr
import com.aleksagn.playlistmaker.data.db.AppDatabase
import com.aleksagn.playlistmaker.data.db.entity.TrackEntity
import com.aleksagn.playlistmaker.domain.api.FavoriteTracksRepository
import com.aleksagn.playlistmaker.domain.models.Track
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class FavoriteTracksRepositoryImpl(
    private val appDatabase: AppDatabase,
    private val trackDbConverter: TrackDbConvertеr
) : FavoriteTracksRepository {

    override suspend fun insertFavoriteTrack(track: Track) {
        appDatabase.trackDao().insertFavoriteTrack(trackDbConverter.map(track))
    }

    override suspend fun deleteFavoriteTrackById(trackId: Int) {
        appDatabase.trackDao().deleteFavoriteTrackById(trackId)
    }

    override fun getFavoriteTracks(): Flow<List<Track>> = flow {
        val tracks = appDatabase.trackDao().getFavoriteTracks()
        emit(convertFromTrackEntity(tracks))
    }

    override fun observeFavoriteTrackById(trackId: Int): Flow<Boolean> = flow {
        emit(appDatabase.trackDao().observeFavoriteTrackById(trackId))
    }

    private fun convertFromTrackEntity(tracks: List<TrackEntity>): List<Track> {
        return tracks.map { track -> trackDbConverter.map(track) }
    }

    private fun convertToTrackEntity(tracks: List<Track>): List<TrackEntity> {
        return tracks.map { track -> trackDbConverter.map(track) }
    }
}
