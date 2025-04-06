package com.aleksagn.playlistmaker.data

interface PreferencesStorage {
    fun getHistoryTracksFromStorage(): String

    fun putHistoryTracksToStorage(json: String)

    fun clearHistoryTracksStorage()

    fun getDarkThemeModeSettingFromStorage(): Boolean

    fun putDarkThemeModeSettingToStorage(mode: Boolean)
}