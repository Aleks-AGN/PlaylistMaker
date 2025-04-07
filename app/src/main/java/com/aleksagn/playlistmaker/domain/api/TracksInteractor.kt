package com.aleksagn.playlistmaker.domain.api

import com.aleksagn.playlistmaker.domain.models.TracksResponse

interface TracksInteractor {
    fun searchTracks(expression: String, consumer: TracksConsumer)

    interface TracksConsumer {
        fun consume(foundTracks: TracksResponse)
    }
}
