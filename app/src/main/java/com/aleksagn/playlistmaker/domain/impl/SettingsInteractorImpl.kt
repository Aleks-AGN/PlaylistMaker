package com.aleksagn.playlistmaker.domain.impl

import com.aleksagn.playlistmaker.domain.api.SettingsInteractor
import com.aleksagn.playlistmaker.domain.api.SettingsRepository

class SettingsInteractorImpl(private val settingsRepository: SettingsRepository) : SettingsInteractor {
    override fun loadDarkThemeModeSetting(): Boolean {
        return settingsRepository.getDarkThemeModeSetting()
    }

    override fun saveDarkThemeModeSetting(mode: Boolean) {
        settingsRepository.putDarkThemeModeSetting(mode)
    }
}
