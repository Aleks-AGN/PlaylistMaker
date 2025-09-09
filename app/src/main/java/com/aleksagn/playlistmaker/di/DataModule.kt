package com.aleksagn.playlistmaker.di

import android.content.Context
import android.content.SharedPreferences
import android.media.MediaPlayer
import com.aleksagn.playlistmaker.data.NetworkClient
import com.aleksagn.playlistmaker.data.StorageClient
import com.aleksagn.playlistmaker.data.network.ITunesApiService
import com.aleksagn.playlistmaker.data.network.RetrofitNetworkClient
import com.aleksagn.playlistmaker.data.storage.CommonPrefsStorageClient
import com.aleksagn.playlistmaker.data.storage.ThemePrefsStorageClient
import com.aleksagn.playlistmaker.domain.models.Track
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

val dataModule = module {

    single<ITunesApiService> {
        Retrofit.Builder()
            .baseUrl("https://itunes.apple.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ITunesApiService::class.java)
    }

    single<SharedPreferences> {
        androidContext()
            .getSharedPreferences("PLAYLIST_MAKER", Context.MODE_PRIVATE)
    }

    single {
        Gson()
    }

    factory {
        MediaPlayer()
    }

    single<NetworkClient> {
        RetrofitNetworkClient(get(), androidContext())
    }

    single<StorageClient<ArrayList<Track>>> {
        CommonPrefsStorageClient(get(), get(), object : TypeToken<ArrayList<Track>>() {}.type)
    }

    single {
        ThemePrefsStorageClient(androidContext(), get())
    }
}
