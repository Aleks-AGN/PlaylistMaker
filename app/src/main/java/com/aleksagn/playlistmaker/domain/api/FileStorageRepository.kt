package com.aleksagn.playlistmaker.domain.api

import android.net.Uri

interface FileStorageRepository {
    fun saveImageToAppPrivateStorage(uri: Uri, playlistTitle: String)
}