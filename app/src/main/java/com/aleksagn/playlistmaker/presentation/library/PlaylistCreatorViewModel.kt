package com.aleksagn.playlistmaker.presentation.library

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aleksagn.playlistmaker.domain.api.PlaylistsInteractor
import com.aleksagn.playlistmaker.domain.models.Playlist
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream

class PlaylistCreatorViewModel(
    private val playlistsInteractor: PlaylistsInteractor,
    private val context: Context
) : ViewModel() {

    fun savePlaylist(playlistTitle: String, playlistDescription: String, imageUri: Uri?) {
        savePlaylistToDataBase(
            Playlist(
                playlistTitle = playlistTitle,
                playlistDescription = playlistDescription,
                playlistImageUri = imageUri
            )
        )
    }

    private fun savePlaylistToDataBase(playlist: Playlist) {
        viewModelScope.launch {
            playlistsInteractor.insertPlaylist(playlist)
        }
    }

    fun saveImage(imageUri: Uri, playlistTitle: String) {
        saveImageToAppPrivateStorage(imageUri, playlistTitle)
    }

    private fun saveImageToAppPrivateStorage(uri: Uri, playlistTitle: String) {
        val contentResolver = context.contentResolver
        val readFlags: Int = Intent.FLAG_GRANT_READ_URI_PERMISSION
        contentResolver.takePersistableUriPermission(uri, readFlags)

        val filePath = File(
            context.getExternalFilesDir(android.os.Environment.DIRECTORY_PICTURES),
            "PlaylistCoverAlbum"
        )

        if (!filePath.exists()) {
            filePath.mkdirs()
        }

        val file = File(filePath, playlistTitle + "_cover.jpg")

        val inputStream = contentResolver.openInputStream(uri)
        val outputStream = FileOutputStream(file)

        BitmapFactory
            .decodeStream(inputStream)
            .compress(Bitmap.CompressFormat.JPEG, 30, outputStream)
    }
}
