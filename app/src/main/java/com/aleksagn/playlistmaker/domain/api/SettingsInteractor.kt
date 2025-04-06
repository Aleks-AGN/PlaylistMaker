package com.aleksagn.playlistmaker.domain.api

interface SettingsInteractor {
    fun loadDarkThemeModeSetting(): Boolean

    fun saveDarkThemeModeSetting(mode: Boolean)
}
