package com.aleksagn.playlistmaker.domain.impl

import com.aleksagn.playlistmaker.domain.api.ThemeSettingInteractor
import com.aleksagn.playlistmaker.domain.api.ThemeSettingRepository

class ThemeSettingInteractorImpl(
    private val repository: ThemeSettingRepository
) : ThemeSettingInteractor {

    override fun saveThemeSetting(mode: Boolean) {
        repository.saveThemeSetting(mode)
    }

    override fun getThemeSetting(): Boolean {
        return repository.getThemeSetting().data!!
    }
}
