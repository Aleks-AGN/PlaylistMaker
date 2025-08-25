package com.aleksagn.playlistmaker.ui.settings.view_model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.aleksagn.playlistmaker.R
import com.aleksagn.playlistmaker.domain.models.EmailData
import com.aleksagn.playlistmaker.util.Creator

class SettingsViewModel() : ViewModel() {

    companion object {
        fun getFactory(): ViewModelProvider.Factory = viewModelFactory {
            initializer {
                SettingsViewModel()
            }
        }
    }

    private val themeSettingInteractor = Creator.provideThemeSettingInteractor()
    private val sharingInteractor = Creator.provideSharingInteractor()

    private val themeModeLiveData = MutableLiveData<Boolean>(themeSettingInteractor.getThemeSetting())
    fun observeThemeMode(): LiveData<Boolean> = themeModeLiveData

    fun switchTheme(isChecked: Boolean) {
        themeModeLiveData.postValue(isChecked)
        themeSettingInteractor.switchTheme(isChecked)
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
        return Creator.getApplication().getString(R.string.share_app_text)
    }

    private fun getSupportEmailData(): EmailData {
        return EmailData(
            Creator.getApplication().getString(R.string.support_email),
            Creator.getApplication().getString(R.string.support_email_subject),
            Creator.getApplication().getString(R.string.support_email_text))
    }

    private fun getTermsLink(): String {
        return Creator.getApplication().getString(R.string.terms_of_use_url)
    }
}
