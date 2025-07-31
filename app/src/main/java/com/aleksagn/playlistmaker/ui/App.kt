package com.aleksagn.playlistmaker.ui

import android.app.Application
import androidx.appcompat.app.AppCompatDelegate
import com.aleksagn.playlistmaker.util.Creator

const val PLAYLIST_MAKER_PREFERENCES = "playlist_maker_preferences"
const val DAY_NIGHT_THEME_KEY = "key_for_day_night_theme"
const val SEARCH_HISTORY_LIST_KEY = "key_for_search_history_list"

class App : Application() {
    var darkTheme = false

    override fun onCreate() {
        super.onCreate()

        Creator.initApplication(this)
        Creator.initGson()
        val settingsInteractor = Creator.provideSettingsInteractor()

        darkTheme = settingsInteractor.loadDarkThemeModeSetting()

        switchTheme(darkTheme)
    }

    fun switchTheme(darkThemeEnabled: Boolean) {
        darkTheme = darkThemeEnabled

        AppCompatDelegate.setDefaultNightMode(
            if (darkThemeEnabled) {
                AppCompatDelegate.MODE_NIGHT_YES
            } else {
                AppCompatDelegate.MODE_NIGHT_NO
            }
        )
        saveTheme()
    }

    fun saveTheme() {
        val settingsInteractor = Creator.provideSettingsInteractor()
        settingsInteractor.saveDarkThemeModeSetting(darkTheme)
    }
}
