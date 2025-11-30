package com.aleksagn.playlistmaker.data.converters

import androidx.core.net.toUri
import com.aleksagn.playlistmaker.data.db.entity.PlaylistEntity
import com.aleksagn.playlistmaker.domain.models.Playlist

class PlaylistDbConverter {
    fun map(playlist: Playlist): PlaylistEntity {
        return PlaylistEntity(
            playlistTitle = playlist.playlistTitle,
            playlistDescription = playlist.playlistDescription,
            playlistImageUri = playlist.playlistImageUri?.toString() ?: "",
            trackList = playlist.trackList,
            trackCount = playlist.trackCount
        )
    }

    fun map(playlist: PlaylistEntity): Playlist {
        return Playlist(
            playlistId = playlist.playlistId,
            playlistTitle = playlist.playlistTitle,
            playlistDescription =playlist.playlistDescription,
            playlistImageUri = playlist.playlistImageUri?.toUri(),
            trackList = playlist.trackList,
            trackCount = playlist.trackCount
        )
    }
}
