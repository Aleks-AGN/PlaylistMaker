package com.aleksagn.playlistmaker.presentation.settings

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.aleksagn.playlistmaker.R
import com.aleksagn.playlistmaker.domain.api.SharingInteractor
import com.aleksagn.playlistmaker.domain.api.ThemeSettingInteractor
import com.aleksagn.playlistmaker.domain.models.EmailData
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class SettingsViewModel(
    private val themeSettingInteractor: ThemeSettingInteractor,
    private val sharingInteractor: SharingInteractor,
    private val context: Context
) : ViewModel() {

//    private val themeModeLiveData = MutableLiveData<Boolean>(themeSettingInteractor.getThemeSetting())
//    fun observeThemeMode(): LiveData<Boolean> = themeModeLiveData

    private val _themeState = MutableStateFlow<Boolean>(false)
    val themeState = _themeState.asStateFlow()

    init {
        _themeState.value = themeSettingInteractor.getThemeSetting()
    }

    fun switchTheme(isChecked: Boolean) {
//        themeModeLiveData.postValue(isChecked)
        _themeState.value = isChecked
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
