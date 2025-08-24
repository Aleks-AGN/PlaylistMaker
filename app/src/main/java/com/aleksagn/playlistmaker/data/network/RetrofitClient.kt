package com.aleksagn.playlistmaker.data.network

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {
    private val itunesBaseUrl = "https://itunes.apple.com/"

    private val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(itunesBaseUrl)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    fun createITunesApiService(): ITunesApiService {
        return retrofit.create(ITunesApiService::class.java)
    }
}
