package com.aleksagn.playlistmaker.presentation.library

import android.content.Context
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aleksagn.playlistmaker.R
import com.aleksagn.playlistmaker.domain.api.FileStorageRepository
import com.aleksagn.playlistmaker.domain.api.PlaylistsInteractor
import com.aleksagn.playlistmaker.domain.models.Playlist
import kotlinx.coroutines.launch

class PlaylistCreatorViewModel(
    private val playlistsInteractor: PlaylistsInteractor,
    private val fileStorageRepository: FileStorageRepository,
    private val context: Context
) : ViewModel() {

    private lateinit var playlist: Playlist

    private var playlistId: Int = -1

    private var fragmentTitle = ""
    private var buttonText = ""

    private var playlistTitle = ""
    private var playlistDescription = ""
    private var imageUri: Uri? = null

    fun getFragmentTitle() = fragmentTitle
    fun getButtonText() = buttonText

    fun getPlaylistTitle() = playlistTitle
    fun getPlaylistDescription() = playlistDescription
    fun getImageUri() = imageUri

    fun setPlaylistTitle(playlistTitle: String) {
        this.playlistTitle = playlistTitle
    }

    fun setPlaylistDescription(playlistDescription: String) {
        this.playlistDescription = playlistDescription
    }

    fun setImageUri(imageUri: Uri) {
        this.imageUri = imageUri
    }

    fun setPlaylistData(playlistId: Int) {
        this.playlistId = playlistId
        if (getEditableStatus()) {
            playlist = playlistsInteractor.getPlaylistById(playlistId!!)
            fragmentTitle = context.getString(R.string.edit)
            buttonText = context.getString(R.string.save_playlist)

            playlistTitle = playlist.playlistTitle
            playlistDescription = playlist.playlistDescription
            imageUri = playlist.playlistImageUri
        }
    }

    fun getEditableStatus(): Boolean {
        return playlistId != -1
    }

    fun isPlaylistHasData() = playlistTitle.isNotEmpty() || playlistDescription.isNotEmpty() || imageUri != null

    fun savePlaylist() {
        if (getEditableStatus()) {
            playlist.playlistTitle = playlistTitle
            playlist.playlistDescription = playlistDescription
            playlist.playlistImageUri = imageUri

            updatePlaylistInDataBase(playlist)
        } else {
            savePlaylistToDataBase(
                Playlist(
                    playlistTitle = playlistTitle,
                    playlistDescription = playlistDescription,
                    playlistImageUri = imageUri
                )
            )
        }

        if (imageUri != null && imageUri.toString().isNotEmpty()) {
            saveImage(imageUri!!, playlistTitle)
        }
    }

    fun getConfirmPlaylistProcessMessage(): String {
        if (getEditableStatus()) {
            return context.getString(R.string.playlist) + " " +
                    playlistTitle + " " + context.getString(R.string.update)
        } else {
            return context.getString(R.string.playlist) + " " +
                    playlistTitle + " " + context.getString(R.string.create)
        }
    }

    private fun savePlaylistToDataBase(playlist: Playlist) {
        viewModelScope.launch {
            playlistsInteractor.insertPlaylist(playlist)
        }
    }

    private fun updatePlaylistInDataBase(playlist: Playlist) {
        viewModelScope.launch {
            playlistsInteractor.updatePlaylist(playlist)
        }
    }

    private fun saveImage(imageUri: Uri, playlistTitle: String) {
        saveImageToAppPrivateStorage(imageUri, playlistTitle)
    }

    private fun saveImageToAppPrivateStorage(uri: Uri, playlistTitle: String) {
        fileStorageRepository.saveImageToAppPrivateStorage(uri, playlistTitle)
    }
}
