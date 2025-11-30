package com.aleksagn.playlistmaker.data.impl

import com.aleksagn.playlistmaker.data.converters.PlaylistDbConverter
import com.aleksagn.playlistmaker.data.converters.TrackInListDbConvertеr
import com.aleksagn.playlistmaker.data.db.AppDatabase
import com.aleksagn.playlistmaker.data.db.entity.PlaylistEntity
import com.aleksagn.playlistmaker.data.db.entity.PlaylistTrackEntity
import com.aleksagn.playlistmaker.domain.api.PlaylistsRepository
import com.aleksagn.playlistmaker.domain.models.Playlist
import com.aleksagn.playlistmaker.domain.models.Track
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.runBlocking

class PlaylistsRepositoryImpl(
    private val appDatabase: AppDatabase,
    private val playlistDbConverter: PlaylistDbConverter,
    private val trackInListDbConverter: TrackInListDbConvertеr
) : PlaylistsRepository {

    override suspend fun insertPlaylist(playlist: Playlist) {
        appDatabase.playlistDao().insertPlaylist(playlistDbConverter.map(playlist))
    }

    override suspend fun insertTrackToPlaylist(track: Track, playlist: Playlist) {
        val playlistChecked = appDatabase.playlistDao().getPlaylistById(playlist.playlistId)
        if (playlistChecked != null) {
            appDatabase.playlistTrackDao().insertPlaylistTrack(
                PlaylistTrackEntity(
                    playlistId = playlistChecked.playlistId,
                    trackId = track.trackId
                )
            )
            appDatabase.trackInListDao().insertTrack(trackInListDbConverter.map(track))
            appDatabase.playlistDao().incrementFieldTrackCount(playlist.playlistId)
        }
    }

    override fun getPlaylists(): Flow<List<Playlist>> = flow {
        val playlists = appDatabase.playlistDao().getPlaylists()
        emit(convertFromPlaylistEntity(playlists))
    }

    override fun getTracksInPlaylist(playlist: Playlist): Flow<List<Track>> {
        TODO("Not yet implemented")
    }

    override fun observeTrackInPlaylistById(trackId: Int, playlistId: Int): Boolean {
        val item: Int
        runBlocking {
            item = appDatabase.playlistTrackDao()
                .getItemByPlaylistIdAndTrackId(trackId, playlistId)?.playlistId ?: -1
        }
        if (item == -1) return false
        else return true
    }

    private fun convertFromPlaylistEntity(playlists: List<PlaylistEntity>): List<Playlist> {
        return playlists.map { playlist -> playlistDbConverter.map(playlist) }
    }

    private fun convertToPlaylistEntity(playlists: List<Playlist>): List<PlaylistEntity> {
        return playlists.map { playlist -> playlistDbConverter.map(playlist) }
    }

    private fun convertToTrackEntity(playlists: List<Playlist>): List<PlaylistEntity> {
        return playlists.map { playlist -> playlistDbConverter.map(playlist) }
    }
}
