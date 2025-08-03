package com.aleksagn.playlistmaker.domain.api

import com.aleksagn.playlistmaker.domain.models.EmailData

interface SharingInteractor {
    fun shareApp(shareAppLink: String)
    fun openSupport(supportEmailData: EmailData)
    fun openTerms(termsLink: String)
}