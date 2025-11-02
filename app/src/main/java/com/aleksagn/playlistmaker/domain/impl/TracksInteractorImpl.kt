package com.aleksagn.playlistmaker.domain.impl

import com.aleksagn.playlistmaker.domain.api.TracksInteractor
import com.aleksagn.playlistmaker.domain.api.TracksRepository
import com.aleksagn.playlistmaker.domain.models.Track
import com.aleksagn.playlistmaker.util.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class TracksInteractorImpl(private val repository: TracksRepository) : TracksInteractor {

    override fun searchTracks(expression: String): Flow<Pair<List<Track>?, String?>> {
        return repository.searchTracks(expression).map { result ->
            when(result) {
                is Resource.Success -> {
                    Pair(result.data, null)
                }
                is Resource.Error -> {
                    Pair(null, result.message)
                }
            }
        }
    }
}
