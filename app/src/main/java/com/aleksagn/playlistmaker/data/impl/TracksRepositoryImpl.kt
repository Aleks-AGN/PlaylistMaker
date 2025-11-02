package com.aleksagn.playlistmaker.data.impl

import com.aleksagn.playlistmaker.data.NetworkClient
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
                val data = (response as TracksSearchResponse).results.mapNotNull { dto ->
                    if (dto.trackId == null || dto.trackId == 0 ||
                        dto.trackName.isNullOrEmpty() ||
                        dto.trackTimeMillis == null || dto.trackTimeMillis == 0L ||
                        dto.artworkUrl100 == null ||
                        dto.previewUrl == null) {
                        null
                    } else {
                        Track(
                            trackId = dto.trackId ,
                            trackName = dto.trackName,
                            artistName = dto.artistName.orEmpty(),
                            trackTimeMillis = dto.trackTimeMillis,
                            artworkUrl100 = dto.artworkUrl100,
                            collectionName = dto.collectionName.orEmpty(),
                            releaseDate = dto.releaseDate?.take(4).orEmpty(),
                            primaryGenreName = dto.primaryGenreName.orEmpty(),
                            country = dto.country.orEmpty(),
                            previewUrl = dto.previewUrl
                        )
                    }
                }
                emit(Resource.Success(data))
            }
            else -> {
                emit(Resource.Error("Ошибка сервера"))
            }
        }
    }
}
