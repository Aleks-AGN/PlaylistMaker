package com.aleksagn.playlistmaker

import java.text.SimpleDateFormat
import java.util.Locale

data class Track(
    val trackId: Int, // Идентификатор трека
    val trackName: String, // Название композиции
    val artistName: String, // Имя исполнителя
    var trackTime: String, // Продолжительность трека
    val trackTimeMillis: Long, // Продолжительность трека в миллисекундах
    val artworkUrl100: String, // Ссылка на изображение обложки
    val collectionName: String, // Название альбома
    val releaseDate: String, // Год релиза трека
    val primaryGenreName: String, // Жанр трека
    val country: String // Страна исполнителя
) {
    fun getFormatTime() = SimpleDateFormat("mm:ss", Locale.getDefault()).format(trackTimeMillis)

    fun getCoverArtwork() = artworkUrl100.replaceAfterLast('/',"512x512bb.jpg")
}
