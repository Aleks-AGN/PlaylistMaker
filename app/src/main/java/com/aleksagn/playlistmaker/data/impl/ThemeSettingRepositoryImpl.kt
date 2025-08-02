package com.aleksagn.playlistmaker.data.impl

import com.aleksagn.playlistmaker.data.storage.ThemePrefsStorageClient
import com.aleksagn.playlistmaker.domain.api.ThemeSettingRepository
import com.aleksagn.playlistmaker.util.Resource

class ThemeSettingRepositoryImpl(
    private val storage: ThemePrefsStorageClient): ThemeSettingRepository {

    override fun saveThemeSetting(mode: Boolean) {
        storage.storeData(mode)
    }

    override fun getThemeSetting(): Resource<Boolean> {
        val mode = storage.getData()
        return Resource.Success(mode)
    }
}
