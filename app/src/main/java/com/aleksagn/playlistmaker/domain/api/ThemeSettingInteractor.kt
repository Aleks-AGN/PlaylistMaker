package com.aleksagn.playlistmaker.domain.api

interface ThemeSettingInteractor {
    fun getThemeSetting(): Boolean
    fun switchTheme(darkThemeEnabled: Boolean)
}