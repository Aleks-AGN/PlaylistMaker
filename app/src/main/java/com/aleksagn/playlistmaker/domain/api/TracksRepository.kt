package com.aleksagn.playlistmaker.domain.api

import com.aleksagn.playlistmaker.domain.models.Track
import com.aleksagn.playlistmaker.util.Resource
import kotlinx.coroutines.flow.Flow

interface TracksRepository {
    fun searchTracks(expression: String): Flow<Resource<List<Track>>>
}