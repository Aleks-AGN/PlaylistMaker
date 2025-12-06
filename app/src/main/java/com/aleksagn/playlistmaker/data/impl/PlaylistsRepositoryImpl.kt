package com.aleksagn.playlistmaker.data.impl

import com.aleksagn.playlistmaker.data.converters.PlaylistDbConverter
import com.aleksagn.playlistmaker.data.converters.TrackInListDbConvertеr
import com.aleksagn.playlistmaker.data.db.AppDatabase
import com.aleksagn.playlistmaker.data.db.entity.PlaylistEntity
import com.aleksagn.playlistmaker.data.db.entity.PlaylistTrackEntity
import com.aleksagn.playlistmaker.data.db.entity.TrackInListEntity
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

    override suspend fun insertTrackToPlaylist(track: Track, playlistId: Int) {
        val playlistChecked = appDatabase.playlistDao().getPlaylistById(playlistId)
        if (playlistChecked != null) {
            appDatabase.playlistTrackDao().insertPlaylistTrack(
                PlaylistTrackEntity(
                    playlistId = playlistChecked.playlistId,
                    trackId = track.trackId
                )
            )
            appDatabase.trackInListDao().insertTrack(trackInListDbConverter.map(track))
            appDatabase.playlistDao().incrementFieldTrackCount(playlistId)
        }
    }

    override fun deleteTrackFromPlaylist(trackId:Int, playlistId:Int) {
        if (observeTrackInPlaylistById(trackId, playlistId)) {
            runBlocking {
                val item = appDatabase.playlistTrackDao().getItemByPlaylistIdAndTrackId(trackId, playlistId)
                if (item != null) {
                    appDatabase.playlistTrackDao().deletePlaylistTrack(item)
                    appDatabase.playlistDao().decrementFieldTracksCount(playlistId)

                    val items = appDatabase.playlistTrackDao().getItemsByTrackId(trackId)
                    if (items.isEmpty()) {
                        appDatabase.trackInListDao().deleteTrackById(trackId)
                    }
                }
            }
        }
    }

    override fun deletePlaylist(playlistId: Int) {
        val items: List<PlaylistTrackEntity?>
        runBlocking {
            items = appDatabase.playlistTrackDao().getItemsByPlaylistId(playlistId)
        }
        if (items.isNotEmpty()) {
            items.forEach {
                deleteTrackFromPlaylist(it!!.trackId, playlistId)
            }
        }
        runBlocking {
            appDatabase.playlistDao().deletePlaylistById(playlistId)
            appDatabase.playlistTrackDao().deleteItemsByPlaylistId(playlistId)
        }
    }

    override fun getPlaylists(): Flow<List<Playlist>> = flow {
        val playlists = appDatabase.playlistDao().getPlaylists()
        emit(convertFromPlaylistEntity(playlists))
    }

    override fun getPlaylistById(playlistId: Int): Playlist {
        var playlistEntity: PlaylistEntity
        runBlocking {
            playlistEntity = appDatabase.playlistDao().getPlaylistById(playlistId) ?: PlaylistEntity()
        }
        return playlistDbConverter.map(playlistEntity)
    }

    override fun getTracksInPlaylist(playlistId: Int): Flow<List<Track>> = flow {
        val trackIdsList: MutableList<Int> = mutableListOf()

        val items: List<PlaylistTrackEntity?> = appDatabase.playlistTrackDao().getItemsByPlaylistId(playlistId)

        if (items.isNotEmpty()) {
            items.forEach {
                trackIdsList.add(it!!.trackId)
            }
            val tracks = appDatabase.trackInListDao().getTracksByIds(trackIdsList)
            emit(convertFromTrackEntity(tracks))
        } else {
            emit(mutableListOf())
        }
    }

    override fun observeTrackInPlaylistById(trackId: Int, playlistId: Int): Boolean {
        var item: Int
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

    private fun convertFromTrackEntity(tracks: List<TrackInListEntity>): List<Track> {
        return tracks.map { track -> trackInListDbConverter.map(track) }
    }
}
