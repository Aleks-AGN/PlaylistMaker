package com.aleksagn.playlistmaker.util

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import com.aleksagn.playlistmaker.data.impl.ExternalNavigatorImpl
import com.aleksagn.playlistmaker.data.impl.PlayerRepositoryImpl
import com.aleksagn.playlistmaker.data.impl.SearchHistoryRepositoryImpl
import com.aleksagn.playlistmaker.data.impl.ThemeSettingRepositoryImpl
import com.aleksagn.playlistmaker.data.impl.TracksRepositoryImpl
import com.aleksagn.playlistmaker.data.network.ITunesApiService
import com.aleksagn.playlistmaker.data.network.RetrofitClient
import com.aleksagn.playlistmaker.data.network.RetrofitNetworkClient
import com.aleksagn.playlistmaker.data.storage.CommonPrefsStorageClient
import com.aleksagn.playlistmaker.data.storage.ThemePrefsStorageClient
import com.aleksagn.playlistmaker.domain.api.PlayerInteractor
import com.aleksagn.playlistmaker.domain.api.PlayerRepository
import com.aleksagn.playlistmaker.domain.api.SearchHistoryInteractor
import com.aleksagn.playlistmaker.domain.api.SearchHistoryRepository
import com.aleksagn.playlistmaker.domain.api.SharingInteractor
import com.aleksagn.playlistmaker.domain.api.ThemeSettingInteractor
import com.aleksagn.playlistmaker.domain.api.ThemeSettingRepository
import com.aleksagn.playlistmaker.domain.api.TracksInteractor
import com.aleksagn.playlistmaker.domain.api.TracksRepository
import com.aleksagn.playlistmaker.domain.impl.PlayerInteractorImpl
import com.aleksagn.playlistmaker.domain.impl.SearchHistoryInteractorImpl
import com.aleksagn.playlistmaker.domain.impl.SharingInteractorImpl
import com.aleksagn.playlistmaker.domain.impl.ThemeSettingInteractorImpl
import com.aleksagn.playlistmaker.domain.impl.TracksInteractorImpl
import com.aleksagn.playlistmaker.domain.models.Track
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

object Creator {
    private lateinit var application: Application
    private lateinit var gson: Gson
    private val iTunesApiService: ITunesApiService = RetrofitClient.createITunesApiService()

    fun initApplication(application: Application) {
        this.application = application
    }

    fun getApplication(): Application {
        return application
    }

    fun initGson() {
        this.gson = Gson()
    }

    fun getGson(): Gson {
        return gson
    }

    private fun provideSharedPreferences(): SharedPreferences {
        return application.getSharedPreferences("PLAYLIST_MAKER", Application.MODE_PRIVATE)
    }

    private fun getTracksRepository(context: Context): TracksRepository {
        return TracksRepositoryImpl(RetrofitNetworkClient(iTunesApiService, context))
    }

    fun provideTracksInteractor(): TracksInteractor {
        return TracksInteractorImpl(getTracksRepository(getApplication()))
    }

    private fun getSearchHistoryRepository(): SearchHistoryRepository {
        return SearchHistoryRepositoryImpl(
            CommonPrefsStorageClient<ArrayList<Track>>(
                provideSharedPreferences(),
                getGson(),
            object : TypeToken<ArrayList<Track>>() {}.type)
        )
    }

    fun provideSearchHistoryInteractor(): SearchHistoryInteractor {
        return SearchHistoryInteractorImpl(getSearchHistoryRepository())
    }

    private fun getPlayerRepository(): PlayerRepository {
        return PlayerRepositoryImpl()
    }

    fun providePlayerInteractor(): PlayerInteractor {
        return PlayerInteractorImpl(getPlayerRepository())
    }

    private fun getThemeSettingRepository(context: Context): ThemeSettingRepository {
        return ThemeSettingRepositoryImpl(
            ThemePrefsStorageClient(
                context,
                provideSharedPreferences())
        )
    }

    fun provideThemeSettingInteractor(): ThemeSettingInteractor {
        return ThemeSettingInteractorImpl(getThemeSettingRepository(getApplication()))
    }

    fun provideSharingInteractor(): SharingInteractor {
        return SharingInteractorImpl(ExternalNavigatorImpl(getApplication()))
    }
}
