package com.aleksagn.playlistmaker.data.impl

import com.aleksagn.playlistmaker.data.NetworkClient
import com.aleksagn.playlistmaker.data.dto.TrackDto
import com.aleksagn.playlistmaker.data.dto.TracksSearchRequest
import com.aleksagn.playlistmaker.data.dto.TracksSearchResponse
import com.aleksagn.playlistmaker.domain.api.TracksRepository
import com.aleksagn.playlistmaker.domain.models.Track
import com.aleksagn.playlistmaker.util.Resource

class TracksRepositoryImpl(private val networkClient: NetworkClient) : TracksRepository {
    override fun searchTracks(expression: String): Resource<List<Track>> {
        val response = networkClient.doRequest(TracksSearchRequest(expression))

        if (response.resultCode == 200) {
            val tracksDto = ArrayList<TrackDto>()
            tracksDto.addAll((response as TracksSearchResponse).results)
            tracksDto.removeAll {
                it.trackName.isNullOrEmpty() || it.collectionName.isNullOrEmpty() ||
                        it.previewUrl.isNullOrEmpty() || it.trackTimeMillis == 0L
                }

            return Resource.Success(tracksDto.map {
                Track(it.trackId, it.trackName, it.artistName, it.trackTimeMillis,
                    it.artworkUrl100, it.collectionName, it.releaseDate,
                    it.primaryGenreName, it.country, it.previewUrl)})
        } else if (response.resultCode == -1) {
            return Resource.Error("Проверьте подключение к интернету")
        } else {
            return Resource.Error("Ошибка сервера")
        }
    }
}
