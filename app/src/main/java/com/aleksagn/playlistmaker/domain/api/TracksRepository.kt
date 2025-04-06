package com.aleksagn.playlistmaker.domain.api

import com.aleksagn.playlistmaker.domain.models.TracksResponse

interface TracksRepository {
    fun searchTracks(expression: String): TracksResponse
}
