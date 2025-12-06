package com.aleksagn.playlistmaker.presentation.library

import com.aleksagn.playlistmaker.domain.models.Playlist
import com.aleksagn.playlistmaker.domain.models.Track

sealed interface PlaylistViewerState {

    object Empty : PlaylistViewerState

    data class Content(
        val playlist: Playlist,
        val tracks: List<Track>,
        val playlistDuration: Int
    ) : PlaylistViewerState

    data class EmptyTracks(
        val playlist: Playlist,
        val playlistDuration: Int
    ) : PlaylistViewerState
}