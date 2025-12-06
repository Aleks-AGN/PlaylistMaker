package com.aleksagn.playlistmaker.domain.api

import com.aleksagn.playlistmaker.domain.models.Playlist
import com.aleksagn.playlistmaker.domain.models.Track
import kotlinx.coroutines.flow.Flow

interface PlaylistsRepository {
    suspend fun insertPlaylist(playlist: Playlist)
    suspend fun insertTrackToPlaylist(track: Track, playlistId: Int)
    fun deleteTrackFromPlaylist(trackId: Int, playlistId: Int)
    fun deletePlaylist(playlistId: Int)
    fun getPlaylists(): Flow<List<Playlist>>
    fun getPlaylistById(playlistId: Int): Playlist
    fun getTracksInPlaylist(playlistId: Int): Flow<List<Track>>
    fun observeTrackInPlaylistById(trackId: Int, playlistId: Int): Boolean
}