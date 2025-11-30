package com.aleksagn.playlistmaker.data.impl

import com.aleksagn.playlistmaker.data.converters.PlaylistDbConverter
import com.aleksagn.playlistmaker.data.converters.TrackDbConvertеr
import com.aleksagn.playlistmaker.data.db.AppDatabase
import com.aleksagn.playlistmaker.data.db.entity.PlaylistEntity
import com.aleksagn.playlistmaker.data.db.entity.TrackEntity
import com.aleksagn.playlistmaker.domain.api.PlaylistsRepository
import com.aleksagn.playlistmaker.domain.models.Playlist
import com.aleksagn.playlistmaker.domain.models.Track
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class PlaylistsRepositoryImpl(
    private val appDatabase: AppDatabase,
    private val playlistDbConverter: PlaylistDbConverter,
    private val trackDbConverter: TrackDbConvertеr
) : PlaylistsRepository {

    override suspend fun insertPlaylist(playlist: Playlist) {
        appDatabase.playlistDao().insertPlaylist(playlistDbConverter.map(playlist))
    }

    override suspend fun insertTrackToPlaylist(track: Track, playlist: Playlist) {
        TODO("Not yet implemented")
    }

    override fun getPlaylists(): Flow<List<Playlist>> = flow {
        val playlists = appDatabase.playlistDao().getPlaylists()
        emit(convertFromPlaylistEntity(playlists))
    }

    override fun getTracksInPlaylist(playlist: Playlist): Flow<List<Track>> {
        TODO("Not yet implemented")
    }

    override fun observeTrackInPlaylistById(trackId: Int, playlistId: Int): Flow<Boolean> {
        TODO("Not yet implemented")
    }

    private fun convertFromPlaylistEntity(playlists: List<PlaylistEntity>): List<Playlist> {
        return playlists.map { playlist -> playlistDbConverter.map(playlist) }
    }

    private fun convertToTrackEntity(playlists: List<Playlist>): List<PlaylistEntity> {
        return playlists.map { playlist -> playlistDbConverter.map(playlist) }
    }
}
