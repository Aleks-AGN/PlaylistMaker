package com.aleksagn.playlistmaker.presentation.library

import com.aleksagn.playlistmaker.domain.models.Playlist

sealed interface PlaylistsState {

    object Empty : PlaylistsState

    data class Content(
        val playlists: List<Playlist>
    ) : PlaylistsState
}