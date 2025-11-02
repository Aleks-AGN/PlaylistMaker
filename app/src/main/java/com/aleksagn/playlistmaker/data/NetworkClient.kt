package com.aleksagn.playlistmaker.data

import com.aleksagn.playlistmaker.data.dto.NetworkResponse

interface NetworkClient {
    suspend fun doRequest(dto: Any): NetworkResponse
}
