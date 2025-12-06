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

    override suspend fun insertTrackToPlaylist(track: Track, playlistId: Int) {
        repository.insertTrackToPlaylist(track, playlistId)
    }

    override fun deleteTrackFromPlaylist(trackId: Int, playlistId: Int) {
        repository.deleteTrackFromPlaylist(trackId, playlistId)
    }

    override fun deletePlaylist(playlistId: Int) {
        repository.deletePlaylist(playlistId)
    }

    override fun getPlaylists(): Flow<List<Playlist>> {
        return repository.getPlaylists()
    }

    override fun getPlaylistById(playlistId: Int): Playlist {
        return repository.getPlaylistById(playlistId)
    }

    override fun getTracksInPlaylist(playlistId: Int): Flow<List<Track>> {
        return repository.getTracksInPlaylist(playlistId)
    }

    override fun observeTrackInPlaylistById(trackId: Int, playlistId: Int): Boolean {
        return repository.observeTrackInPlaylistById(trackId, playlistId)
    }
}
