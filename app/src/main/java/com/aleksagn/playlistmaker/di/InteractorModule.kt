package com.aleksagn.playlistmaker.di

import com.aleksagn.playlistmaker.domain.api.FavoriteTracksInteractor
import com.aleksagn.playlistmaker.domain.api.PlayerInteractor
import com.aleksagn.playlistmaker.domain.api.SearchHistoryInteractor
import com.aleksagn.playlistmaker.domain.api.SharingInteractor
import com.aleksagn.playlistmaker.domain.api.ThemeSettingInteractor
import com.aleksagn.playlistmaker.domain.api.TracksInteractor
import com.aleksagn.playlistmaker.domain.impl.FavoriteTracksInteractorImpl
import com.aleksagn.playlistmaker.domain.impl.PlayerInteractorImpl
import com.aleksagn.playlistmaker.domain.impl.SearchHistoryInteractorImpl
import com.aleksagn.playlistmaker.domain.impl.SharingInteractorImpl
import com.aleksagn.playlistmaker.domain.impl.ThemeSettingInteractorImpl
import com.aleksagn.playlistmaker.domain.impl.TracksInteractorImpl
import org.koin.dsl.module

val interactorModule = module {

    single<TracksInteractor> {
        TracksInteractorImpl(get())
    }

    single<SearchHistoryInteractor> {
        SearchHistoryInteractorImpl(get())
    }

    single<FavoriteTracksInteractor> {
        FavoriteTracksInteractorImpl(get())
    }

    factory<PlayerInteractor> {
        PlayerInteractorImpl(get())
    }

    single<ThemeSettingInteractor> {
        ThemeSettingInteractorImpl(get())
    }

    single<SharingInteractor> {
        SharingInteractorImpl(get())
    }
}
