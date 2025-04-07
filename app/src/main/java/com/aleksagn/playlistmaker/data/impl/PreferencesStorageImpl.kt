package com.aleksagn.playlistmaker.data.impl

import android.content.Context
import android.content.SharedPreferences
import android.content.res.Configuration
import com.aleksagn.playlistmaker.creator.Creator
import com.aleksagn.playlistmaker.data.PreferencesStorage
import com.aleksagn.playlistmaker.ui.DAY_NIGHT_THEME_KEY
import com.aleksagn.playlistmaker.ui.SEARCH_HISTORY_LIST_KEY

class PreferencesStorageImpl(private val sharedPreferences: SharedPreferences) : PreferencesStorage {
    override fun getHistoryTracksFromStorage(): String {
        return sharedPreferences.getString(SEARCH_HISTORY_LIST_KEY, null) ?: ""
    }

    override fun putHistoryTracksToStorage(json: String) {
        sharedPreferences.edit()
            .putString(SEARCH_HISTORY_LIST_KEY,json)
            .apply()
    }

    override fun clearHistoryTracksStorage() {
        sharedPreferences.edit()
            .remove(SEARCH_HISTORY_LIST_KEY)
            .apply()
    }

    override fun getDarkThemeModeSettingFromStorage(): Boolean {
        return sharedPreferences.getBoolean(DAY_NIGHT_THEME_KEY, isDarkTheme(Creator.getApplication()))
    }

    override fun putDarkThemeModeSettingToStorage(mode: Boolean) {
        sharedPreferences.edit()
            .putBoolean(DAY_NIGHT_THEME_KEY, mode)
            .apply()
    }

    private fun isDarkTheme(context: Context): Boolean {
        return when (context.resources?.configuration?.uiMode?.and(Configuration.UI_MODE_NIGHT_MASK)) {
            Configuration.UI_MODE_NIGHT_YES -> true
            else -> false
        }
    }
}
