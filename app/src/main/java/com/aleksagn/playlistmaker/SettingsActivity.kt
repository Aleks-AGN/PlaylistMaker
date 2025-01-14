package com.aleksagn.playlistmaker

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.google.android.material.switchmaterial.SwitchMaterial

class SettingsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        val toolbar = findViewById<Toolbar>(R.id.settings_toolbar)
        toolbar.setNavigationOnClickListener {
            finish()
        }

        val btnThemeSwitcher = findViewById<SwitchMaterial>(R.id.theme_switcher)
        btnThemeSwitcher.isChecked = getSharedPreferences(PLAYLIST_MAKER_PREFERENCES, MODE_PRIVATE)
            .getBoolean(DAY_NIGHT_THEME_KEY, (applicationContext as App).darkTheme)
        btnThemeSwitcher.setOnCheckedChangeListener { _, isChecked ->
            (applicationContext as App).switchTheme(isChecked)
            (applicationContext as App).saveTheme()
        }

        val btnShare = findViewById<TextView>(R.id.text_view_share)
        btnShare.setOnClickListener {
            val shareIntent = Intent(Intent.ACTION_SEND)
            shareIntent.type = "text/plain"
            shareIntent.putExtra(Intent.EXTRA_TEXT, getString(R.string.share_app_text))
            startActivity(Intent.createChooser(shareIntent, null))
        }

        val btnSupport = findViewById<TextView>(R.id.text_view_support)
        btnSupport.setOnClickListener {
            val supportIntent = Intent(Intent.ACTION_SENDTO)
            supportIntent.data = Uri.parse("mailto:")
            supportIntent.putExtra(Intent.EXTRA_EMAIL, arrayOf(getString(R.string.support_email)))
            supportIntent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.support_email_subject))
            supportIntent.putExtra(Intent.EXTRA_TEXT, getString(R.string.support_email_text))
            startActivity(supportIntent)
        }

        val btnTerms = findViewById<TextView>(R.id.text_view_terms)
        btnTerms.setOnClickListener {
            val termsOfUseUrl = Uri.parse(getString(R.string.terms_of_use_url))
            val termsOfUseIntent = Intent(Intent.ACTION_VIEW, termsOfUseUrl)
            startActivity(termsOfUseIntent)
        }
    }
}
