package com.aleksagn.playlistmaker.data.storage

import android.content.Context
import android.content.SharedPreferences
import android.content.res.Configuration

class ThemePrefsStorageClient(
    private val context: Context,
    private val prefs: SharedPreferences,
) {

    private val dataKey = "DAY_NIGHT_THEME_KEY"

    fun storeData(data: Boolean) {
        prefs.edit().putBoolean(dataKey, data).apply()
    }

    fun getData(): Boolean {
        return prefs.getBoolean(dataKey, isDarkTheme(context))
    }

    private fun isDarkTheme(context: Context): Boolean {
        return when (context.resources?.configuration?.uiMode?.and(Configuration.UI_MODE_NIGHT_MASK)) {
            Configuration.UI_MODE_NIGHT_YES -> true
            else -> false
        }
    }
}
