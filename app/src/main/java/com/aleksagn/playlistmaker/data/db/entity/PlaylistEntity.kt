package com.aleksagn.playlistmaker.data.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "playlists")
data class PlaylistEntity(
    @PrimaryKey(autoGenerate = true)
    val playlistId: Int = 0, // Идентификатор плейлиста (первичный ключ)
    var playlistTitle: String, // Название плейлиста
    var playlistDescription: String = "", // Описание плейлиста
    var playlistImageUri: String = "", // Путь к файлу изображения для обложки
    var trackList: String = "", // Список идентификаторов треков, которые будут добавляться в этот плейлист
    var trackCount: Int = 0 // Количество треков, добавленных в плейлист
) {
    constructor(): this(
        playlistId = 0,
        playlistTitle = "",
        playlistDescription = "",
        playlistImageUri = "",
        trackList = "",
        trackCount = 0
    )
}
