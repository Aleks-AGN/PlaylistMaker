package com.aleksagn.playlistmaker.data.network

import com.aleksagn.playlistmaker.data.dto.TracksSearchResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface ITunesApiService {
    @GET("/search")
    suspend fun searchTracks(@Query("term") text: String): TracksSearchResponse
}