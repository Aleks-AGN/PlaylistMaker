package com.aleksagn.playlistmaker.ui.library

import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.aleksagn.playlistmaker.R
import com.aleksagn.playlistmaker.domain.models.Track
import com.aleksagn.playlistmaker.presentation.library.FavoritesViewModel
import com.aleksagn.playlistmaker.presentation.library.PlaylistsViewModel
import com.aleksagn.playlistmaker.ui.theme.LibraryTheme

@Composable
fun LibraryScreen(
    favoritesViewModel: FavoritesViewModel,
    playlistsViewModel: PlaylistsViewModel,
    navigateToPlayer: (Track) -> Unit,
    navigateToViewer: (Int) -> Unit,
    onNewPlaylistButtonClick: () -> Unit
) {
    LibraryTheme(darkTheme = isSystemInDarkTheme()) {
        LibraryContent(
            favoritesViewModel,
            playlistsViewModel,
            navigateToPlayer,
            navigateToViewer,
            onNewPlaylistButtonClick
        )
    }
}

@Composable
fun LibraryContent(
    favoritesViewModel: FavoritesViewModel,
    playlistsViewModel: PlaylistsViewModel,
    navigateToPlayer: (Track) -> Unit,
    navigateToViewer: (Int) -> Unit,
    onNewPlaylistButtonClick: () -> Unit
) {

    var selectedTabIndex by rememberSaveable { mutableStateOf(0) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .systemBarsPadding()
    ) {
        Text(
            text = stringResource(R.string.library),
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.padding(horizontal = 16.dp).padding(top = 16.dp, bottom = 12.dp)
        )

        TabRow(
            selectedTabIndex = selectedTabIndex,
            containerColor = MaterialTheme.colorScheme.background,
            contentColor = MaterialTheme.colorScheme.surface,
            indicator = { tabPositions ->
                TabRowDefaults.Indicator(
                    modifier = Modifier.tabIndicatorOffset(tabPositions[selectedTabIndex]),
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
        ) {
            Tab(
                selected = selectedTabIndex == 0,
                selectedContentColor = MaterialTheme.colorScheme.surface,
                unselectedContentColor = MaterialTheme.colorScheme.surface,
                onClick = {
                    selectedTabIndex = 0
                },
                text = {
                    Text(
                        text = stringResource(R.string.favorites),
                        style = MaterialTheme.typography.bodyMedium.copy(fontSize = 14.sp),
                    )
                }
            )

            Tab(
                selected = selectedTabIndex == 1,
                selectedContentColor = MaterialTheme.colorScheme.surface,
                unselectedContentColor = MaterialTheme.colorScheme.surface,
                onClick = {
                    selectedTabIndex = 1
                },
                text = {
                    Text(
                        text = stringResource(R.string.playlists),
                        style = MaterialTheme.typography.bodyMedium.copy(fontSize = 14.sp),
                    )
                }
            )
        }

        when (selectedTabIndex) {
            0 -> FavoritesScreen(
                favoritesViewModel,
                navigateToPlayer
            )

            1 -> PlaylistsScreen(
                playlistsViewModel,
                navigateToViewer,
                onNewPlaylistButtonClick
            )
        }
    }
}
