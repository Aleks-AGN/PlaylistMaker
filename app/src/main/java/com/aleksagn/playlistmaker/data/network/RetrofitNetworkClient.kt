package com.aleksagn.playlistmaker.data.network

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import com.aleksagn.playlistmaker.data.NetworkClient
import com.aleksagn.playlistmaker.data.dto.NetworkResponse
import com.aleksagn.playlistmaker.data.dto.TracksSearchRequest

class RetrofitNetworkClient(private val iTunesApiService: ITunesApiService, private val context: Context) : NetworkClient {

    override fun doRequest(dto: Any): NetworkResponse {
        if (isConnected() == false) {
            return NetworkResponse().apply { resultCode = -1 }
        }
        if (dto !is TracksSearchRequest) {
            return NetworkResponse().apply { resultCode = 400 }
        }

        val response = iTunesApiService.searchTracks(dto.expression).execute()
        val body = response.body()
        return if (body != null) {
            body.apply { resultCode = response.code() }
        } else {
            NetworkResponse().apply { resultCode = response.code() }
        }
    }

    private fun isConnected(): Boolean {
        val connectivityManager = context.getSystemService(
            Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val capabilities = connectivityManager.getNetworkCapabilities(connectivityManager.activeNetwork)
        if (capabilities != null) {
            when {
                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> return true
                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> return true
                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> return true
            }
        }
        return false
    }
}
