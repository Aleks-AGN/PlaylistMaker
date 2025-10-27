package com.aleksagn.playlistmaker.data.network

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import com.aleksagn.playlistmaker.data.NetworkClient
import com.aleksagn.playlistmaker.data.dto.NetworkResponse
import com.aleksagn.playlistmaker.data.dto.TracksSearchRequest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class RetrofitNetworkClient(private val iTunesApiService: ITunesApiService, private val context: Context) : NetworkClient {

    override suspend fun doRequest(dto: Any): NetworkResponse {
        if (isConnected() == false) {
            return NetworkResponse().apply { resultCode = -1 }
        }
        if (dto !is TracksSearchRequest) {
            return NetworkResponse().apply { resultCode = 400 }
        }

        return withContext(Dispatchers.IO) {
            try {
                val response = iTunesApiService.searchTracks(dto.expression)
                response.apply { resultCode = 200 }
            } catch (e: Throwable) {
                NetworkResponse().apply { resultCode = 500 }
            }
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
