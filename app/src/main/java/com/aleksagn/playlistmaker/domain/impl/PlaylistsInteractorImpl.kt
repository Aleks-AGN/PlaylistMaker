package com.aleksagn.playlistmaker.domain.impl

import com.aleksagn.playlistmaker.domain.api.PlaylistsInteractor
import com.aleksagn.playlistmaker.domain.api.PlaylistsRepository
import com.aleksagn.playlistmaker.domain.models.Playlist
import com.aleksagn.playlistmaker.domain.models.Track
import kotlinx.coroutines.flow.Flow

class PlaylistsInteractorImpl(
    private val repository: PlaylistsRepository
) : PlaylistsInteractor {

    override suspend fun insertPlaylist(playlist: Playlist) {
        repository.insertPlaylist(playlist)
    }

    override suspend fun insertTrackToPlaylist(track: Track, playlist: Playlist) {
        repository.insertTrackToPlaylist(track, playlist)
    }

    override fun getPlaylists(): Flow<List<Playlist>> {
        return repository.getPlaylists()
    }

    override fun getTracksInPlaylist(playlist: Playlist): Flow<List<Track>> {
        return repository.getTracksInPlaylist(playlist)
    }

    override fun observeTrackInPlaylistById(trackId: Int, playlistId: Int): Boolean {
        return repository.observeTrackInPlaylistById(trackId, playlistId)
    }
}
