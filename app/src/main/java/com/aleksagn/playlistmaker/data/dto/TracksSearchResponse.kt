package com.aleksagn.playlistmaker.data.dto

class TracksSearchResponse(
    val resultCount: Int,
    val results: List<TrackDto>
) : NetworkResponse()
