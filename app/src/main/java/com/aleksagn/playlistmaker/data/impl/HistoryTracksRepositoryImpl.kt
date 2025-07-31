package com.aleksagn.playlistmaker.data.impl

import com.aleksagn.playlistmaker.util.Creator
import com.aleksagn.playlistmaker.data.PreferencesStorage
import com.aleksagn.playlistmaker.domain.api.HistoryTracksRepository
import com.aleksagn.playlistmaker.domain.models.Track

class HistoryTracksRepositoryImpl(private val preferencesStorage: PreferencesStorage) :
    HistoryTracksRepository {
    override fun getHistoryTracks(): ArrayList<Track> {
        val json = preferencesStorage.getHistoryTracksFromStorage()

        if (json.isNullOrEmpty()) {
            return ArrayList()
        } else {
            val arr = Creator.getGson().fromJson(json, Array<Track>::class.java)
            return ArrayList(arr.toMutableList())
        }
    }

    override fun putHistoryTracks(tracks: ArrayList<Track>) {
        val json = Creator.getGson().toJson(tracks.toTypedArray())
        preferencesStorage.putHistoryTracksToStorage(json)
    }

    override fun clearHistoryTracks() {
        preferencesStorage.clearHistoryTracksStorage()
    }
}
