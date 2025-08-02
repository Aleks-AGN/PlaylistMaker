package com.aleksagn.playlistmaker.domain.api

import com.aleksagn.playlistmaker.util.Resource

interface ThemeSettingRepository {
    fun saveThemeSetting(mode: Boolean)
    fun getThemeSetting(): Resource<Boolean>
}