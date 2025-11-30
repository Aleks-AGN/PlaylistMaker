package com.aleksagn.playlistmaker.presentation.library

import com.aleksagn.playlistmaker.domain.models.Playlist

interface PlaylistsState {
    data class Content(
        val playlists: List<Playlist>
    ) : PlaylistsState

    data class Empty(
        val message: String
    ) : PlaylistsState
}