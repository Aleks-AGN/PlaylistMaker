package com.aleksagn.playlistmaker.domain.api

import com.aleksagn.playlistmaker.domain.models.Track
import com.aleksagn.playlistmaker.util.Resource

interface TracksRepository {
    fun searchTracks(expression: String): Resource<List<Track>>
}
