package com.aleksagn.playlistmaker.ui.library

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.aleksagn.playlistmaker.R
import com.aleksagn.playlistmaker.presentation.library.PlaylistsState
import com.aleksagn.playlistmaker.presentation.library.PlaylistsViewModel

@Composable
fun PlaylistsScreen(
    viewModel: PlaylistsViewModel,
    navigateToViewer: (Int) -> Unit,
    onNewPlaylistButtonClick: () -> Unit,
) {

    val state = viewModel.state.collectAsState().value
    var throwPlaylistToViewer by remember { mutableStateOf<Int?>(null) }

    LaunchedEffect(throwPlaylistToViewer) {
        throwPlaylistToViewer?.let { playlistId ->
            navigateToViewer(playlistId)
        }
    }

    Column(modifier = Modifier.fillMaxSize()) {
        Button(
            onClick = onNewPlaylistButtonClick,
            modifier = Modifier
                .padding(start = 0.dp, top = 24.dp, end = 0.dp, bottom = 16.dp)
                .align(Alignment.CenterHorizontally),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.onSurface,
                contentColor = MaterialTheme.colorScheme.background
            )
        ) {
            Text(text = stringResource(R.string.btn_new_playlist))
        }

        when (state) {
            is PlaylistsState.Empty -> {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(MaterialTheme.colorScheme.background)
                        .padding(top = 46.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier
                            .size(120.dp)
                            .background(MaterialTheme.colorScheme.background)
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.ic_empty_search),
                            contentDescription = null,
                            modifier = Modifier.fillMaxSize()
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = stringResource(R.string.empty_playlists),
                        textAlign = TextAlign.Center,
                        style = MaterialTheme.typography.bodyLarge.copy(
                            fontFamily = FontFamily(Font(R.font.ys_display_medium)),
                            fontSize = 19.sp,
                            color = MaterialTheme.colorScheme.primary
                        ),
                        color = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }

            is PlaylistsState.Content -> {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 12.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    contentPadding = PaddingValues(bottom = 16.dp)
                ) {
                    items(state.playlists) { playlist ->
                        PlaylistItem(
                            playlist = playlist,
                            onClick = { throwPlaylistToViewer = playlist.playlistId }
                        )
                    }
                }
            }
        }
    }
}
