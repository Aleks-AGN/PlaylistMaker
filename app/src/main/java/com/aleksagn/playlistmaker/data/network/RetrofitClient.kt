package com.aleksagn.playlistmaker.data.network

import com.aleksagn.playlistmaker.ui.ITUNES_BASE_URL
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {
    private val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(ITUNES_BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    fun createITunesApiService(): ITunesApiService {
        return retrofit.create(ITunesApiService::class.java)
    }
}
