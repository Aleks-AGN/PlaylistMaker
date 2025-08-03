package com.aleksagn.playlistmaker.ui.settings.view_model

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.aleksagn.playlistmaker.R
import com.aleksagn.playlistmaker.domain.models.EmailData
import com.aleksagn.playlistmaker.ui.App
import com.aleksagn.playlistmaker.util.Creator

class SettingsViewModel(private val context: Context) : ViewModel() {

    companion object {
        fun getFactory(): ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val app = (this[APPLICATION_KEY] as App)
                SettingsViewModel(app)
            }
        }
    }

    private val themeSettingInteractor = Creator.provideThemeSettingInteractor(context)
    private val sharingInteractor = Creator.provideSharingInteractor(context)

    private val themeModeLiveData = MutableLiveData<Boolean>(themeSettingInteractor.getThemeSetting())
    fun observeThemeMode(): LiveData<Boolean> = themeModeLiveData

    fun switchTheme(isChecked: Boolean) {
        themeModeLiveData.postValue(isChecked)
        (context.applicationContext as App).switchTheme(isChecked)
        (context.applicationContext as App).saveTheme()
    }

    fun shareApp() {
        sharingInteractor.shareApp(getShareAppLink())
    }

    fun openSupport() {
        sharingInteractor.openSupport(getSupportEmailData())
    }

    fun openTerms() {
        sharingInteractor.openTerms(getTermsLink())
    }

    private fun getShareAppLink(): String {
        return context.getString(R.string.share_app_text)
    }

    private fun getSupportEmailData(): EmailData {
        return EmailData(
            context.getString(R.string.support_email),
            context.getString(R.string.support_email_subject),
            context.getString(R.string.support_email_text))
    }

    private fun getTermsLink(): String {
        return context.getString(R.string.terms_of_use_url)
    }
}
