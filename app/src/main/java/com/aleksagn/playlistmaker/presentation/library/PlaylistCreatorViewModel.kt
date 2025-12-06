package com.aleksagn.playlistmaker.presentation.library

import android.content.Context
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aleksagn.playlistmaker.domain.api.FileStorageRepository
import com.aleksagn.playlistmaker.domain.api.PlaylistsInteractor
import com.aleksagn.playlistmaker.domain.models.Playlist
import kotlinx.coroutines.launch

class PlaylistCreatorViewModel(
    private val playlistsInteractor: PlaylistsInteractor,
    private val fileStorageRepository: FileStorageRepository,
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
        fileStorageRepository.saveImageToAppPrivateStorage(uri, playlistTitle)
    }
}
