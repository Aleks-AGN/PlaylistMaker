package com.aleksagn.playlistmaker.data.impl

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import com.aleksagn.playlistmaker.domain.api.FileStorageRepository
import java.io.File
import java.io.FileOutputStream

class FileStorageRepositoryImpl(private val context: Context): FileStorageRepository {
    override fun saveImageToAppPrivateStorage(uri: Uri, playlistTitle: String) {
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
