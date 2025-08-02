package com.aleksagn.playlistmaker.domain.api

interface ThemeSettingInteractor {
    fun saveThemeSetting(mode: Boolean)
    fun getThemeSetting(): Boolean
}