package com.aleksagn.playlistmaker.data.impl

import com.aleksagn.playlistmaker.data.NetworkClient
import com.aleksagn.playlistmaker.data.dto.TrackDto
import com.aleksagn.playlistmaker.data.dto.TracksSearchRequest
import com.aleksagn.playlistmaker.data.dto.TracksSearchResponse
import com.aleksagn.playlistmaker.domain.api.TracksRepository
import com.aleksagn.playlistmaker.domain.models.Track
import com.aleksagn.playlistmaker.util.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class TracksRepositoryImpl(private val networkClient: NetworkClient) : TracksRepository {
    override fun searchTracks(expression: String): Flow<Resource<List<Track>>> = flow {
        val response = networkClient.doRequest(TracksSearchRequest(expression))

        when (response.resultCode) {
            -1 -> {
                emit(Resource.Error("Проверьте подключение к интернету"))
            }
            200 -> {
                with(response as TracksSearchResponse) {
                    val tracksDto = ArrayList<TrackDto>()
                    tracksDto.addAll(response.results)
                    tracksDto.removeAll {
                        it.trackName.isNullOrEmpty() || it.collectionName.isNullOrEmpty() ||
                                it.previewUrl.isNullOrEmpty() || it.trackTimeMillis == 0L
                    }

                    val data = tracksDto.map {
                        Track(it.trackId, it.trackName, it.artistName, it.trackTimeMillis,
                                it.artworkUrl100, it.collectionName, it.releaseDate,
                                it.primaryGenreName, it.country, it.previewUrl)
                    }
                    emit(Resource.Success(data))
                }
            }
            else -> {
                emit(Resource.Error("Ошибка сервера"))
            }
        }
    }
}
