package com.aleksagn.playlistmaker.ui.settings

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.aleksagn.playlistmaker.R
import com.aleksagn.playlistmaker.util.Creator
import com.aleksagn.playlistmaker.databinding.ActivitySettingsBinding
import com.aleksagn.playlistmaker.ui.App

class SettingsActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySettingsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.settingsToolbar.setNavigationOnClickListener {
            finish()
        }

        val settingsInteractor = Creator.provideSettingsInteractor()

        binding.themeSwitcher.isChecked = settingsInteractor.loadDarkThemeModeSetting()
        binding.themeSwitcher.setOnCheckedChangeListener { _, isChecked ->
            (applicationContext as App).switchTheme(isChecked)
            (applicationContext as App).saveTheme()
        }

        binding.textViewShare.setOnClickListener {
            val shareIntent = Intent(Intent.ACTION_SEND)
            shareIntent.type = "text/plain"
            shareIntent.putExtra(Intent.EXTRA_TEXT, getString(R.string.share_app_text))
            startActivity(Intent.createChooser(shareIntent, null))
        }

        binding.textViewSupport.setOnClickListener {
            val supportIntent = Intent(Intent.ACTION_SENDTO)
            supportIntent.data = Uri.parse("mailto:")
            supportIntent.putExtra(Intent.EXTRA_EMAIL, arrayOf(getString(R.string.support_email)))
            supportIntent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.support_email_subject))
            supportIntent.putExtra(Intent.EXTRA_TEXT, getString(R.string.support_email_text))
            startActivity(supportIntent)
        }

        binding.textViewTerms.setOnClickListener {
            val termsOfUseUrl = Uri.parse(getString(R.string.terms_of_use_url))
            val termsOfUseIntent = Intent(Intent.ACTION_VIEW, termsOfUseUrl)
            startActivity(termsOfUseIntent)
        }
    }
}
