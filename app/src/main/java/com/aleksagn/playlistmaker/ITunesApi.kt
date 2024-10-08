package com.aleksagn.playlistmaker

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface ITunesApi {
    @GET("/search")
    fun searchTrack(
        @Query("term") text: String
    ): Call<TracksResponse>
}
