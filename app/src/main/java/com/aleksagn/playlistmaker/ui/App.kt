package com.aleksagn.playlistmaker.ui

import android.app.Application
import androidx.appcompat.app.AppCompatDelegate
import com.aleksagn.playlistmaker.util.Creator

class App : Application() {
    var darkTheme = false

    override fun onCreate() {
        super.onCreate()

//        Creator.initApplication(this)
        Creator.initGson()
        val themeSettingInteractor = Creator.provideThemeSettingInteractor(this)

        darkTheme = themeSettingInteractor.getThemeSetting()
        switchTheme(darkTheme)
    }

    fun switchTheme(darkThemeEnabled: Boolean) {
        darkTheme = darkThemeEnabled

        AppCompatDelegate.setDefaultNightMode(
            if (darkThemeEnabled) { AppCompatDelegate.MODE_NIGHT_YES }
            else { AppCompatDelegate.MODE_NIGHT_NO }
        )
        saveTheme()
    }

    fun saveTheme() {
        val themeSettingInteractor = Creator.provideThemeSettingInteractor(this)
        themeSettingInteractor.saveThemeSetting(darkTheme)
    }
}
