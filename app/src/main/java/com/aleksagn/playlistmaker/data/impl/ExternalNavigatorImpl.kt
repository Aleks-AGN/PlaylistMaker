package com.aleksagn.playlistmaker.data.impl

import android.content.Context
import android.content.Intent
import android.net.Uri
import com.aleksagn.playlistmaker.domain.api.ExternalNavigator
import com.aleksagn.playlistmaker.domain.models.EmailData

class ExternalNavigatorImpl(private val context: Context) : ExternalNavigator {

    override fun shareLink(shareAppLink: String) {
        val shareIntent = Intent(Intent.ACTION_SEND)
        shareIntent.type = "text/plain"
        shareIntent.putExtra(Intent.EXTRA_TEXT, shareAppLink)
        val chooserIntent = Intent.createChooser(shareIntent, null)
        chooserIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        context.startActivity(chooserIntent)
    }

    override fun openEmail(supportEmailData: EmailData) {
        val supportIntent = Intent(Intent.ACTION_SENDTO)
        supportIntent.data = Uri.parse("mailto:")
        supportIntent.putExtra(Intent.EXTRA_EMAIL, arrayOf(supportEmailData.email))
        supportIntent.putExtra(Intent.EXTRA_SUBJECT, supportEmailData.subject)
        supportIntent.putExtra(Intent.EXTRA_TEXT, supportEmailData.text)
        supportIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        context.startActivity(supportIntent)
    }

    override fun openLink(termsLink: String) {
        val termsOfUseUrl = Uri.parse(termsLink)
        val termsOfUseIntent = Intent(Intent.ACTION_VIEW, termsOfUseUrl)
        termsOfUseIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        context.startActivity(termsOfUseIntent)
    }
}
