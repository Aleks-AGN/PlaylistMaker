package com.aleksagn.playlistmaker.domain.api

import com.aleksagn.playlistmaker.util.Resource

interface ThemeSettingRepository {
    fun getThemeSetting(): Resource<Boolean>
    fun switchTheme(darkThemeEnabled: Boolean)
}