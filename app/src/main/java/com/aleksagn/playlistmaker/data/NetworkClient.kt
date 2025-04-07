package com.aleksagn.playlistmaker.data

import com.aleksagn.playlistmaker.data.dto.NetworkResponse

interface NetworkClient {
    fun doRequest(dto: Any): NetworkResponse
}
