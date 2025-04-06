package com.aleksagn.playlistmaker.domain.api

import com.aleksagn.playlistmaker.domain.models.Track

interface HistoryTracksRepository {
    fun getHistoryTracks(): ArrayList<Track>

    fun putHistoryTracks(tracks: ArrayList<Track>)

    fun clearHistoryTracks()
}