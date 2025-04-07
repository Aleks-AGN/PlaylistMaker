package com.aleksagn.playlistmaker.creator

import android.app.Application
import android.content.SharedPreferences
import com.aleksagn.playlistmaker.data.PreferencesStorage
import com.aleksagn.playlistmaker.data.impl.HistoryTracksRepositoryImpl
import com.aleksagn.playlistmaker.data.impl.PlayerRepositoryImpl
import com.aleksagn.playlistmaker.data.impl.PreferencesStorageImpl
import com.aleksagn.playlistmaker.data.impl.SettingsRepositoryImpl
import com.aleksagn.playlistmaker.data.impl.TracksRepositoryImpl
import com.aleksagn.playlistmaker.data.network.ITunesApiService
import com.aleksagn.playlistmaker.data.network.RetrofitClient
import com.aleksagn.playlistmaker.data.network.RetrofitNetworkClient
import com.aleksagn.playlistmaker.domain.api.HistoryTracksRepository
import com.aleksagn.playlistmaker.domain.api.PlayerInteractor
import com.aleksagn.playlistmaker.domain.api.PlayerRepository
import com.aleksagn.playlistmaker.domain.api.SettingsInteractor
import com.aleksagn.playlistmaker.domain.api.SettingsRepository
import com.aleksagn.playlistmaker.domain.api.TracksInteractor
import com.aleksagn.playlistmaker.domain.api.TracksRepository
import com.aleksagn.playlistmaker.domain.impl.PlayerInteractorImpl
import com.aleksagn.playlistmaker.domain.impl.SettingsInteractorImpl
import com.aleksagn.playlistmaker.domain.impl.TracksInteractorImpl
import com.aleksagn.playlistmaker.ui.PLAYLIST_MAKER_PREFERENCES
import com.google.gson.Gson

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
        return application.getSharedPreferences(PLAYLIST_MAKER_PREFERENCES, Application.MODE_PRIVATE)
    }

    private fun getTracksRepository(): TracksRepository {
        return TracksRepositoryImpl(RetrofitNetworkClient(iTunesApiService))
    }

    fun provideTracksInteractor(): TracksInteractor {
        return TracksInteractorImpl(getTracksRepository())
    }

    fun getPreferencesStorage(): PreferencesStorage {
        return PreferencesStorageImpl(provideSharedPreferences())
    }

    fun getHistoryTracksRepository(): HistoryTracksRepository {
        return HistoryTracksRepositoryImpl(getPreferencesStorage())
    }

    private fun getPlayerRepository(): PlayerRepository {
        return PlayerRepositoryImpl()
    }

    fun providePlayerInteractor(): PlayerInteractor {
        return PlayerInteractorImpl(getPlayerRepository())
    }

    fun getSettingsRepository(): SettingsRepository {
        return SettingsRepositoryImpl(getPreferencesStorage())
    }

    fun provideSettingsInteractor(): SettingsInteractor {
        return SettingsInteractorImpl(getSettingsRepository())
    }
}
