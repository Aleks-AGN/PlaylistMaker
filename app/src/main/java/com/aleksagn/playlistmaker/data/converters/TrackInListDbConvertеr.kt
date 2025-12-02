package com.aleksagn.playlistmaker.data.converters

import com.aleksagn.playlistmaker.data.db.entity.TrackInListEntity
import com.aleksagn.playlistmaker.domain.models.Track

class TrackInListDbConvertеr {
    fun map(track: Track, currentTime: Long = System.currentTimeMillis()): TrackInListEntity {
        return TrackInListEntity(
            trackId = track.trackId,
            trackName = track.trackName,
            artistName = track.artistName,
            trackTimeMillis = track.trackTimeMillis,
            artworkUrl100 = track.artworkUrl100,
            collectionName = track.collectionName,
            releaseDate = track.releaseDate,
            primaryGenreName = track.primaryGenreName,
            country = track.country,
            previewUrl = track.previewUrl,
            additionTime = currentTime
        )
    }

    fun map(track: TrackInListEntity): Track {
        return Track(
            trackId = track.trackId,
            trackName = track.trackName,
            artistName = track.artistName,
            trackTimeMillis = track.trackTimeMillis,
            artworkUrl100 = track.artworkUrl100,
            collectionName = track.collectionName,
            releaseDate = track.releaseDate,
            primaryGenreName = track.primaryGenreName,
            country = track.country,
            previewUrl = track.previewUrl
        )
    }
}
