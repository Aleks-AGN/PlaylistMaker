package com.aleksagn.playlistmaker.data.network

import com.aleksagn.playlistmaker.data.NetworkClient
import com.aleksagn.playlistmaker.data.dto.NetworkResponse
import com.aleksagn.playlistmaker.data.dto.TracksSearchRequest

class RetrofitNetworkClient(private val iTunesApiService: ITunesApiService) : NetworkClient {

    override fun doRequest(dto: Any): NetworkResponse {
        if (dto is TracksSearchRequest) {
            return try {
                val response = iTunesApiService.searchTracks(dto.expression).execute()

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
