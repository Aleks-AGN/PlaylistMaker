package com.aleksagn.playlistmaker.di

import com.aleksagn.playlistmaker.ui.library.view_model.FavoritesViewModel
import com.aleksagn.playlistmaker.ui.library.view_model.PlaylistsViewModel
import com.aleksagn.playlistmaker.ui.player.view_model.PlayerViewModel
import com.aleksagn.playlistmaker.ui.search.view_model.SearchViewModel
import com.aleksagn.playlistmaker.ui.settings.view_model.SettingsViewModel
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val viewModelModule = module {

    viewModel {
        PlayerViewModel(get())
    }

    viewModel {
        SearchViewModel(get(), get(), androidContext())
    }

    viewModel {
        SettingsViewModel(get(), get(), androidContext())
    }

    viewModel {
        FavoritesViewModel()
    }

    viewModel {
        PlaylistsViewModel()
    }
}
