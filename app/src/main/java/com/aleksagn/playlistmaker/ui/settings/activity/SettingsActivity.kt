package com.aleksagn.playlistmaker.ui.settings.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.aleksagn.playlistmaker.databinding.ActivitySettingsBinding
import com.aleksagn.playlistmaker.ui.settings.view_model.SettingsViewModel

class SettingsActivity : AppCompatActivity() {

    private var viewModel: SettingsViewModel? = null

    private lateinit var binding: ActivitySettingsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel = ViewModelProvider(this, SettingsViewModel.getFactory())
            .get(SettingsViewModel::class.java)

        viewModel?.observeThemeMode()?.observe(this) {
            binding.themeSwitcher.isChecked = it
        }

        binding.settingsToolbar.setNavigationOnClickListener {
            finish()
        }

        binding.themeSwitcher.setOnCheckedChangeListener { _, isChecked ->
            viewModel?.switchTheme(isChecked)
        }

        binding.textViewShare.setOnClickListener {
            viewModel?.shareApp()
        }

        binding.textViewSupport.setOnClickListener {
            viewModel?.openSupport()
        }

        binding.textViewTerms.setOnClickListener {
            viewModel?.openTerms()
        }
    }
}
