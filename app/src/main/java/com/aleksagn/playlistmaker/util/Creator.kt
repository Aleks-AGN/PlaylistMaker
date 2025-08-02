package com.aleksagn.playlistmaker.util

import android.app.Application
import android.content.Context
import com.aleksagn.playlistmaker.data.impl.PlayerRepositoryImpl
import com.aleksagn.playlistmaker.data.impl.SearchHistoryRepositoryImpl
import com.aleksagn.playlistmaker.data.impl.ThemeSettingRepositoryImpl
import com.aleksagn.playlistmaker.data.impl.TracksRepositoryImpl
import com.aleksagn.playlistmaker.data.network.RetrofitNetworkClient
import com.aleksagn.playlistmaker.data.storage.CommonPrefsStorageClient
import com.aleksagn.playlistmaker.data.storage.ThemePrefsStorageClient
import com.aleksagn.playlistmaker.domain.api.PlayerInteractor
import com.aleksagn.playlistmaker.domain.api.PlayerRepository
import com.aleksagn.playlistmaker.domain.api.SearchHistoryInteractor
import com.aleksagn.playlistmaker.domain.api.SearchHistoryRepository
import com.aleksagn.playlistmaker.domain.api.ThemeSettingInteractor
import com.aleksagn.playlistmaker.domain.api.ThemeSettingRepository
import com.aleksagn.playlistmaker.domain.api.TracksInteractor
import com.aleksagn.playlistmaker.domain.api.TracksRepository
import com.aleksagn.playlistmaker.domain.impl.PlayerInteractorImpl
import com.aleksagn.playlistmaker.domain.impl.SearchHistoryInteractorImpl
import com.aleksagn.playlistmaker.domain.impl.ThemeSettingInteractorImpl
import com.aleksagn.playlistmaker.domain.impl.TracksInteractorImpl
import com.aleksagn.playlistmaker.domain.models.Track
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

object Creator {
    private lateinit var application: Application
    private lateinit var gson: Gson

    fun initApplication(application: Application) {
        this.application = application
    }

//    fun getApplication(): Application {
//        return application
//    }

    fun initGson() {
        this.gson = Gson()
    }

    fun getGson(): Gson {
        return gson
    }

    private fun getTracksRepository(context: Context): TracksRepository {
        return TracksRepositoryImpl(RetrofitNetworkClient(context))
    }

    fun provideTracksInteractor(): TracksInteractor {
        return TracksInteractorImpl(getTracksRepository(application))
    }

    private fun getSearchHistoryRepository(context: Context): SearchHistoryRepository {
        return SearchHistoryRepositoryImpl(
            CommonPrefsStorageClient<ArrayList<Track>>(
            context,
            "SEARCH_HISTORY_LIST_KEY",
            object : TypeToken<ArrayList<Track>>() {}.type)
        )
    }

    fun provideSearchHistoryInteractor(): SearchHistoryInteractor {
        return SearchHistoryInteractorImpl(getSearchHistoryRepository(application))
    }

    private fun getThemeSettingRepository(context: Context): ThemeSettingRepository {
        return ThemeSettingRepositoryImpl(
            ThemePrefsStorageClient(
                context,
                "DAY_NIGHT_THEME_KEY")
        )
    }

    fun provideThemeSettingInteractor(): ThemeSettingInteractor {
        return ThemeSettingInteractorImpl(getThemeSettingRepository(application))
    }

    private fun getPlayerRepository(): PlayerRepository {
        return PlayerRepositoryImpl()
    }

    fun providePlayerInteractor(): PlayerInteractor {
        return PlayerInteractorImpl(getPlayerRepository())
    }
}
