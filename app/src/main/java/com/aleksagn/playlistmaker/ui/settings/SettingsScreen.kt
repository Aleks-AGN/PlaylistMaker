package com.aleksagn.playlistmaker.ui.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.aleksagn.playlistmaker.R
import com.aleksagn.playlistmaker.presentation.settings.SettingsViewModel
import com.aleksagn.playlistmaker.ui.theme.SettingsTheme

@Composable
fun SettingsScreen(
    viewModel: SettingsViewModel
) {
    val isDarkTheme by viewModel.themeState.collectAsState()

    SettingsTheme(darkTheme = isDarkTheme) {
        SettingsContent(
            isDarkTheme = isDarkTheme,
            onThemeSwitchChanged = { viewModel.switchTheme(it) },
            onShareClick = { viewModel.shareApp() },
            onSupportClick = { viewModel.openSupport() },
            onTermsClick = { viewModel.openTerms() }
        )
    }
}

@Composable
fun SettingsContent(
    isDarkTheme: Boolean,
    onThemeSwitchChanged: (Boolean) -> Unit,
    onShareClick: () -> Unit,
    onSupportClick: () -> Unit,
    onTermsClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp)
            .systemBarsPadding()
    ) {
        Text(
            text = stringResource(R.string.settings),
            style = MaterialTheme.typography.headlineMedium.copy(
                color = MaterialTheme.colorScheme.onSurface,
            ),
            modifier = Modifier.padding(bottom = 44.dp)
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onThemeSwitchChanged(!isDarkTheme) }
                .padding(vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = stringResource(R.string.dark_theme),
                style = MaterialTheme.typography.bodyLarge.copy(
                    color = MaterialTheme.colorScheme.onSurface,
                    fontSize = 16.sp,
                    fontFamily = FontFamily(Font(R.font.ys_display_regular))
                ),
                modifier = Modifier.weight(1f)
            )
            Switch(
                checked = isDarkTheme,
                onCheckedChange = onThemeSwitchChanged,
                colors = SwitchDefaults.colors(
                    checkedThumbColor = MaterialTheme.colorScheme.onSecondary,
                    uncheckedThumbColor = MaterialTheme.colorScheme.primary,
                    checkedTrackColor = MaterialTheme.colorScheme.secondary,
                    uncheckedTrackColor = MaterialTheme.colorScheme.onPrimary,
                )
            )
        }

        SettingsItem(stringResource(R.string.share_app), Icons.Default.Share, onShareClick)

        SettingsItem(stringResource(R.string.write_to_support), Icons.Default.Email, onSupportClick)

        SettingsItem(stringResource(R.string.terms_of_use), Icons.Default.ChevronRight, onTermsClick)
    }
}

@Composable
fun SettingsItem(
    text: String,
    icon: ImageVector,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = 20.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.bodyLarge.copy(
                color = MaterialTheme.colorScheme.onSurface,
                fontSize = 16.sp,
                fontFamily = FontFamily(Font(R.font.ys_display_regular))
            ),
            modifier = Modifier.weight(1f)
        )
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.tertiary
        )
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun SettingsContentPreview() {
    SettingsContent(
        isDarkTheme = false,
        onThemeSwitchChanged = {},
        onShareClick = {},
        onSupportClick = {},
        onTermsClick = {}
    )
}
