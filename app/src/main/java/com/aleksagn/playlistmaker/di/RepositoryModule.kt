package com.aleksagn.playlistmaker.di

import com.aleksagn.playlistmaker.data.converters.PlaylistDbConverter
import com.aleksagn.playlistmaker.data.converters.TrackDbConvertеr
import com.aleksagn.playlistmaker.data.converters.TrackInListDbConvertеr
import com.aleksagn.playlistmaker.data.impl.ExternalNavigatorImpl
import com.aleksagn.playlistmaker.data.impl.FavoriteTracksRepositoryImpl
import com.aleksagn.playlistmaker.data.impl.PlayerRepositoryImpl
import com.aleksagn.playlistmaker.data.impl.PlaylistsRepositoryImpl
import com.aleksagn.playlistmaker.data.impl.SearchHistoryRepositoryImpl
import com.aleksagn.playlistmaker.data.impl.ThemeSettingRepositoryImpl
import com.aleksagn.playlistmaker.data.impl.TracksRepositoryImpl
import com.aleksagn.playlistmaker.domain.api.ExternalNavigator
import com.aleksagn.playlistmaker.domain.api.FavoriteTracksRepository
import com.aleksagn.playlistmaker.domain.api.PlayerRepository
import com.aleksagn.playlistmaker.domain.api.PlaylistsRepository
import com.aleksagn.playlistmaker.domain.api.SearchHistoryRepository
import com.aleksagn.playlistmaker.domain.api.ThemeSettingRepository
import com.aleksagn.playlistmaker.domain.api.TracksRepository
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val repositoryModule = module {

    single<TracksRepository> {
        TracksRepositoryImpl(get(), get())
    }

    single<SearchHistoryRepository> {
        SearchHistoryRepositoryImpl(get(), get())
    }

    factory {
        TrackDbConvertеr()
    }

    factory {
        TrackInListDbConvertеr()
    }

    factory {
        PlaylistDbConverter()
    }

    single<FavoriteTracksRepository> {
        FavoriteTracksRepositoryImpl(get(), get())
    }

    single<PlaylistsRepository> {
        PlaylistsRepositoryImpl(get(), get(), get())
    }

    factory<PlayerRepository> {
        PlayerRepositoryImpl(get())
    }

    single<ThemeSettingRepository> {
        ThemeSettingRepositoryImpl(get())
    }

    single<ExternalNavigator> {
        ExternalNavigatorImpl(androidContext())
    }
}
