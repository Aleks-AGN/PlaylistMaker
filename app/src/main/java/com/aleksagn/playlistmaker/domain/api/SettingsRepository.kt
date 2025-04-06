package com.aleksagn.playlistmaker.domain.api

interface SettingsRepository {
    fun getDarkThemeModeSetting(): Boolean

    fun putDarkThemeModeSetting(mode: Boolean)
}