package com.aleksagn.playlistmaker.ui

import android.app.Application
import com.aleksagn.playlistmaker.di.dataModule
import com.aleksagn.playlistmaker.di.interactorModule
import com.aleksagn.playlistmaker.di.repositoryModule
import com.aleksagn.playlistmaker.di.viewModelModule
import com.aleksagn.playlistmaker.domain.api.ThemeSettingInteractor
import org.koin.android.ext.android.inject
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.GlobalContext.startKoin

class App : Application() {

    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@App)
            modules(dataModule, repositoryModule, interactorModule, viewModelModule)
        }

        val themeSettingInteractor: ThemeSettingInteractor by inject()
        themeSettingInteractor.switchTheme(themeSettingInteractor.getThemeSetting())
    }
}
