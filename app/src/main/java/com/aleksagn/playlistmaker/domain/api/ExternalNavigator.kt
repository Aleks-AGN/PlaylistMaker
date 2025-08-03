package com.aleksagn.playlistmaker.domain.api

import com.aleksagn.playlistmaker.domain.models.EmailData

interface ExternalNavigator {
    fun shareLink(shareAppLink: String)
    fun openEmail(supportEmailData: EmailData)
    fun openLink(termsLink: String)
}