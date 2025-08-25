package com.aleksagn.playlistmaker.domain.impl

import com.aleksagn.playlistmaker.domain.api.ExternalNavigator
import com.aleksagn.playlistmaker.domain.api.SharingInteractor
import com.aleksagn.playlistmaker.domain.models.EmailData

class SharingInteractorImpl(
    private val externalNavigator: ExternalNavigator,
) : SharingInteractor {

    override fun shareApp(shareAppLink: String) {
        externalNavigator.shareLink(shareAppLink)
    }

    override fun openSupport(supportEmailData: EmailData) {
        externalNavigator.openEmail(supportEmailData)
    }

    override fun openTerms(termsLink: String) {
        externalNavigator.openLink(termsLink)
    }
}
