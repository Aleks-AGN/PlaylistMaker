package com.aleksagn.playlistmaker.di

import com.aleksagn.playlistmaker.presentation.library.FavoritesViewModel
import com.aleksagn.playlistmaker.presentation.library.PlaylistCreatorViewModel
import com.aleksagn.playlistmaker.presentation.library.PlaylistsViewModel
import com.aleksagn.playlistmaker.presentation.player.PlayerViewModel
import com.aleksagn.playlistmaker.presentation.search.SearchViewModel
import com.aleksagn.playlistmaker.presentation.settings.SettingsViewModel
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val viewModelModule = module {

    viewModel {
        PlayerViewModel(get(), get(), get())
    }

    viewModel {
        SearchViewModel(get(), get(), androidContext())
    }

    viewModel {
        SettingsViewModel(get(), get(), androidContext())
    }

    viewModel {
        FavoritesViewModel(get(), get())
    }

    viewModel {
        PlaylistsViewModel(get(), get())
    }

    viewModel {
        PlaylistCreatorViewModel(get(), get())
    }
}
