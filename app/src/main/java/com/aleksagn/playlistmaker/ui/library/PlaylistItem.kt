package com.aleksagn.playlistmaker.ui.library

import android.annotation.SuppressLint
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.aleksagn.playlistmaker.R
import com.aleksagn.playlistmaker.domain.models.Playlist

@SuppressLint("LocalContextResourcesRead")
@Composable
fun PlaylistItem(
    playlist: Playlist,
    onClick: () -> Unit
) {
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(1f),
            shape = RoundedCornerShape(8.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.background)
        ) {
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(playlist.playlistImageUri.takeIf { playlist.playlistImageUri != null && playlist.playlistImageUri.toString().isNotEmpty() }
                        ?: R.drawable.ic_playlist_viewer_placeholder)
                    .crossfade(true)
                    .build(),
                contentDescription = null,
                contentScale = if (playlist.playlistImageUri != null && playlist.playlistImageUri.toString().isNotEmpty()) ContentScale.Fit else ContentScale.Inside,
                modifier = Modifier.fillMaxSize()
            )
        }

        Text(
            text = playlist.playlistTitle,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.padding(top = 4.dp)
        )

        Text(
            text = "${playlist.trackCount} " + context.resources.getQuantityString(R.plurals.plural_tracks, playlist.trackCount),
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}
