package com.aleksagn.playlistmaker.data.impl

import androidx.appcompat.app.AppCompatDelegate
import com.aleksagn.playlistmaker.data.StorageClient
import com.aleksagn.playlistmaker.data.storage.ThemePrefsStorageClient
import com.aleksagn.playlistmaker.domain.api.ThemeSettingRepository
import com.aleksagn.playlistmaker.util.Resource

class ThemeSettingRepositoryImpl(
    private val storage: ThemePrefsStorageClient): ThemeSettingRepository {
//    private val storage: StorageClient<Boolean>
//): ThemeSettingRepository {

    override fun getThemeSetting(): Resource<Boolean> {
        val mode = storage.getData()!!
        return Resource.Success(mode)
    }

    override fun switchTheme(darkThemeEnabled: Boolean) {
        AppCompatDelegate.setDefaultNightMode(
            if (darkThemeEnabled) { AppCompatDelegate.MODE_NIGHT_YES }
            else { AppCompatDelegate.MODE_NIGHT_NO }
        )
        storage.storeData(darkThemeEnabled)
    }
}
