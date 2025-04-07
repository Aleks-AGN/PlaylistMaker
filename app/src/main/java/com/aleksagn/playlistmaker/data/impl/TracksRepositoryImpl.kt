package com.aleksagn.playlistmaker.data.impl

import com.aleksagn.playlistmaker.data.NetworkClient
import com.aleksagn.playlistmaker.data.dto.TrackDto
import com.aleksagn.playlistmaker.data.dto.TracksSearchRequest
import com.aleksagn.playlistmaker.data.dto.TracksSearchResponse
import com.aleksagn.playlistmaker.domain.api.TracksRepository
import com.aleksagn.playlistmaker.domain.models.Track
import com.aleksagn.playlistmaker.domain.models.TracksResponse

class TracksRepositoryImpl(private val networkClient: NetworkClient) : TracksRepository {
    override fun searchTracks(expression: String): TracksResponse {
        val response = networkClient.doRequest(TracksSearchRequest(expression))
        val tracks = ArrayList<TrackDto>()
        var results: List<Track> = emptyList()

        if (response.resultCode == 200) {
            tracks.addAll((response as TracksSearchResponse).results)
            tracks.removeAll { it.trackName.isNullOrEmpty() || it.collectionName.isNullOrEmpty() ||
                    it.previewUrl.isNullOrEmpty() || it.trackTimeMillis == 0L }

            //results = (response as TracksSearchResponse).results.map {
            results = tracks.map {
                Track(
                    it.trackId,
                    it.trackName,
                    it.artistName,
                    it.trackTimeMillis,
                    it.artworkUrl100,
                    it.collectionName,
                    it.releaseDate,
                    it.primaryGenreName,
                    it.country,
                    it.previewUrl)
            }
        }
        return TracksResponse(response.resultCode, results)
    }
}
