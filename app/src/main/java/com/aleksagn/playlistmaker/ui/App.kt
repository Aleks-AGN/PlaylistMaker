package com.aleksagn.playlistmaker.ui

import android.app.Application
import com.aleksagn.playlistmaker.util.Creator

class App : Application() {
    var darkTheme = false

    override fun onCreate() {
        super.onCreate()

        Creator.initApplication(this)
        Creator.initGson()

        val themeSettingInteractor = Creator.provideThemeSettingInteractor()
        darkTheme = themeSettingInteractor.getThemeSetting()
        themeSettingInteractor.switchTheme(darkTheme)
    }
}
