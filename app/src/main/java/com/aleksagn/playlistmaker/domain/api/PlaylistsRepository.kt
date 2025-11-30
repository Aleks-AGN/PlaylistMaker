package com.aleksagn.playlistmaker.domain.api

import com.aleksagn.playlistmaker.domain.models.Playlist
import com.aleksagn.playlistmaker.domain.models.Track
import kotlinx.coroutines.flow.Flow

interface PlaylistsRepository {
    suspend fun insertPlaylist(playlist: Playlist)
    suspend fun insertTrackToPlaylist(track: Track, playlist: Playlist)
    fun getPlaylists(): Flow<List<Playlist>>
    fun getTracksInPlaylist(playlist: Playlist): Flow<List<Track>>
    fun observeTrackInPlaylistById(trackId: Int, playlistId: Int): Flow<Boolean>
}