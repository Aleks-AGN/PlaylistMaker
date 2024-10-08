package com.aleksagn.playlistmaker

data class Track(
    val trackId: Int, // Идентификатор трека
    val trackName: String, // Название композиции
    val artistName: String, // Имя исполнителя
    var trackTime: String, // Продолжительность трека
    val trackTimeMillis: Long, // Продолжительность трека в миллисекундах
    val artworkUrl100: String // Ссылка на изображение обложки
)
