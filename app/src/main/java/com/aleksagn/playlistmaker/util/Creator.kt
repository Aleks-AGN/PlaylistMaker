package com.aleksagn.playlistmaker.util

import android.content.Context
import com.aleksagn.playlistmaker.data.impl.ExternalNavigatorImpl
import com.aleksagn.playlistmaker.data.impl.SearchHistoryRepositoryImpl
import com.aleksagn.playlistmaker.data.impl.ThemeSettingRepositoryImpl
import com.aleksagn.playlistmaker.data.impl.TracksRepositoryImpl
import com.aleksagn.playlistmaker.data.network.RetrofitNetworkClient
import com.aleksagn.playlistmaker.data.storage.CommonPrefsStorageClient
import com.aleksagn.playlistmaker.data.storage.ThemePrefsStorageClient
import com.aleksagn.playlistmaker.domain.api.SearchHistoryInteractor
import com.aleksagn.playlistmaker.domain.api.SearchHistoryRepository
import com.aleksagn.playlistmaker.domain.api.SharingInteractor
import com.aleksagn.playlistmaker.domain.api.ThemeSettingInteractor
import com.aleksagn.playlistmaker.domain.api.ThemeSettingRepository
import com.aleksagn.playlistmaker.domain.api.TracksInteractor
import com.aleksagn.playlistmaker.domain.api.TracksRepository
import com.aleksagn.playlistmaker.domain.impl.SearchHistoryInteractorImpl
import com.aleksagn.playlistmaker.domain.impl.SharingInteractorImpl
import com.aleksagn.playlistmaker.domain.impl.ThemeSettingInteractorImpl
import com.aleksagn.playlistmaker.domain.impl.TracksInteractorImpl
import com.aleksagn.playlistmaker.domain.models.Track
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

object Creator {

    private lateinit var gson: Gson

    fun initGson() {
        this.gson = Gson()
    }

    fun getGson(): Gson {
        return gson
    }

    private fun getTracksRepository(context: Context): TracksRepository {
        return TracksRepositoryImpl(RetrofitNetworkClient(context))
    }

    fun provideTracksInteractor(context: Context): TracksInteractor {
        return TracksInteractorImpl(getTracksRepository(context))
    }

    private fun getSearchHistoryRepository(context: Context): SearchHistoryRepository {
        return SearchHistoryRepositoryImpl(
            CommonPrefsStorageClient<ArrayList<Track>>(
            context,
            "SEARCH_HISTORY_LIST_KEY",
            object : TypeToken<ArrayList<Track>>() {}.type)
        )
    }

    fun provideSearchHistoryInteractor(context: Context): SearchHistoryInteractor {
        return SearchHistoryInteractorImpl(getSearchHistoryRepository(context))
    }

    private fun getThemeSettingRepository(context: Context): ThemeSettingRepository {
        return ThemeSettingRepositoryImpl(
            ThemePrefsStorageClient(
                context,
                "DAY_NIGHT_THEME_KEY")
        )
    }

    fun provideThemeSettingInteractor(context: Context): ThemeSettingInteractor {
        return ThemeSettingInteractorImpl(getThemeSettingRepository(context))
    }

    fun provideSharingInteractor(context: Context): SharingInteractor {
        return SharingInteractorImpl(ExternalNavigatorImpl(context))
    }
}
