package com.aleksagn.playlistmaker.util

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.widget.Toast
import com.aleksagn.playlistmaker.R

class ConnectivityBroadcastReceiver : BroadcastReceiver() {

    companion object {
        const val ACTION = "android.net.conn.CONNECTIVITY_CHANGE"
    }

    override fun onReceive(context: Context?, intent: Intent?) {
        if (intent?.action == ACTION) {
            val isConnected = isConnected(context)

            if (!isConnected) {
                Toast.makeText(context, R.string.net_connection_error, Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun isConnected(context: Context?): Boolean {
        val connectivityManager = context?.getSystemService(
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
