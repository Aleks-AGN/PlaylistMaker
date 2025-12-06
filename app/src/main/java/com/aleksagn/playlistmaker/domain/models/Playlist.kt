package com.aleksagn.playlistmaker.domain.models

import android.net.Uri

data class Playlist(
    var playlistId: Int = 0, // Идентификатор плейлиста
    var playlistTitle: String, // Название плейлиста
    var playlistDescription: String = "", // Описание плейлиста
    var playlistImageUri: Uri? = null, // Путь к файлу изображения для обложки
    var trackList: String = "", // Список идентификаторов треков, которые будут добавляться в этот плейлист
    var trackCount: Int = 0 // Количество треков, добавленных в плейлист
) {
    constructor(): this(
        playlistId = 0,
        playlistTitle = "",
        playlistDescription = "",
        playlistImageUri = null,
        trackList = "",
        trackCount = 0
    )
}
