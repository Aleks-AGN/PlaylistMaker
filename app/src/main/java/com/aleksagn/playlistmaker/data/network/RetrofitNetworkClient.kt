package com.aleksagn.playlistmaker.data.network

import com.aleksagn.playlistmaker.data.NetworkClient
import com.aleksagn.playlistmaker.data.dto.NetworkResponse
import com.aleksagn.playlistmaker.data.dto.TracksSearchRequest
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class RetrofitNetworkClient : NetworkClient {
    companion object {
        private const val ITUNES_BASE_URL = "https://itunes.apple.com/"
    }

    private val retrofit = Retrofit.Builder()
        .baseUrl(ITUNES_BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private val iTunesService = retrofit.create(ITunesApiService::class.java)

    override fun doRequest(dto: Any): NetworkResponse {
        if (dto is TracksSearchRequest) {
            return try {
                val response = iTunesService.searchTracks(dto.expression).execute()

                val body = response.body() ?: NetworkResponse()

                return body.apply { resultCode = response.code() }
            } catch (ex: Exception) {
                NetworkResponse().apply { resultCode = -1 }
            }
        } else {
            return NetworkResponse().apply { resultCode = 400 }
        }
    }
}
