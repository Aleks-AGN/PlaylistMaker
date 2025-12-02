package com.aleksagn.playlistmaker.data.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "playlists_tracks")
data class TrackInListEntity(
    @PrimaryKey
    val trackId: Int, // Идентификатор трека (первичный ключ)
    val trackName: String, // Название композиции
    val artistName: String, // Имя исполнителя
    val trackTimeMillis: Long, // Продолжительность трека в миллисекундах
    val artworkUrl100: String, // Ссылка на изображение обложки
    val collectionName: String, // Название альбома
    val releaseDate: String, // Год релиза трека
    val primaryGenreName: String, // Жанр трека
    val country: String, // Страна исполнителя
    val previewUrl: String, // Ссылка на отрывок трека
    val additionTime : Long // Время добавления трека
)
