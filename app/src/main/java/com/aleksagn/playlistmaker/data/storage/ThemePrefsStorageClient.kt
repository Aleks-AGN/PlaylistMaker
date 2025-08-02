package com.aleksagn.playlistmaker.data.storage

import android.content.Context
import android.content.SharedPreferences
import android.content.res.Configuration
import com.aleksagn.playlistmaker.data.StorageClient

class ThemePrefsStorageClient(
    private val context: Context,
    private val dataKey: String,
) : StorageClient<Boolean> {

    private val prefs: SharedPreferences = context.getSharedPreferences("PLAYLIST_MAKER", Context.MODE_PRIVATE)

    override fun storeData(data: Boolean) {
        prefs.edit().putBoolean(dataKey, data).apply()
    }

    override fun getData(): Boolean {
        return prefs.getBoolean(dataKey, isDarkTheme(context))
    }

    override fun clearData() {
        prefs.edit().remove(dataKey).apply()
    }

    private fun isDarkTheme(context: Context): Boolean {
        return when (context.resources?.configuration?.uiMode?.and(Configuration.UI_MODE_NIGHT_MASK)) {
            Configuration.UI_MODE_NIGHT_YES -> true
            else -> false
        }
    }
}
