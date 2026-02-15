package com.aleksagn.playlistmaker.ui.library

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.aleksagn.playlistmaker.R
import com.aleksagn.playlistmaker.domain.models.Track
import com.aleksagn.playlistmaker.presentation.library.FavoritesState
import com.aleksagn.playlistmaker.presentation.library.FavoritesViewModel
import com.aleksagn.playlistmaker.ui.search.TrackItem

@Composable
fun FavoritesScreen(
    viewModel: FavoritesViewModel,
    navigateToPlayer: (Track) -> Unit
) {
    val state = viewModel.state.collectAsState().value
    var throwTrackToPlayer by remember { mutableStateOf<Track?>(null) }

    LaunchedEffect(throwTrackToPlayer) {
        throwTrackToPlayer?.let { track ->
            navigateToPlayer(track)
        }
    }

    when (state) {
        is FavoritesState.Empty -> {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = 106.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Image(
                    painter = painterResource(id = R.drawable.ic_empty_search),
                    contentDescription = null
                )
                Text(
                    text = stringResource(R.string.empty_media_library),
                    style = MaterialTheme.typography.bodyLarge.copy(
                        fontFamily = FontFamily(Font(R.font.ys_display_medium)),
                        fontSize = 19.sp,
                        color = MaterialTheme.colorScheme.primary
                    ),
                    modifier = Modifier.padding(top = 16.dp)
                )

            }
        }

        is FavoritesState.Content -> {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(vertical = 16.dp),
                contentPadding = PaddingValues(horizontal = 13.dp)
            ) {
                items(state.tracks) { track ->
                    TrackItem(
                        track = track,
                        onClick = { throwTrackToPlayer = track }
                    )
                }
            }
        }
    }
}
