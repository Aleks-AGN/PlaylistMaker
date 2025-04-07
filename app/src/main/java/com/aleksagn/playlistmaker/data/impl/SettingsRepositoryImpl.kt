package com.aleksagn.playlistmaker.data.impl

import com.aleksagn.playlistmaker.data.PreferencesStorage
import com.aleksagn.playlistmaker.domain.api.SettingsRepository

class SettingsRepositoryImpl(private val preferencesStorage: PreferencesStorage) : SettingsRepository {
    override fun getDarkThemeModeSetting(): Boolean {
        return preferencesStorage.getDarkThemeModeSettingFromStorage()
    }

    override fun putDarkThemeModeSetting(mode: Boolean) {
        preferencesStorage.putDarkThemeModeSettingToStorage(mode)
    }
}
