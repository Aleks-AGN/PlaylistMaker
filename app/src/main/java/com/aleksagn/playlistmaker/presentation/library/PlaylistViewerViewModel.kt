package com.aleksagn.playlistmaker.presentation.library

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aleksagn.playlistmaker.R
import com.aleksagn.playlistmaker.domain.api.PlaylistsInteractor
import com.aleksagn.playlistmaker.domain.api.SharingInteractor
import com.aleksagn.playlistmaker.domain.models.Playlist
import com.aleksagn.playlistmaker.domain.models.Track
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Locale

class PlaylistViewerViewModel(
    private val playlistsInteractor: PlaylistsInteractor,
    private val sharingInteractor: SharingInteractor,
    private val context: Context
) : ViewModel() {

    private var playlistId: Int = 0

    private val stateLiveData = MutableLiveData<PlaylistViewerState>()
    fun observeState(): LiveData<PlaylistViewerState> = stateLiveData

    fun setPlaylist(playlistId: Int) {
        this.playlistId = playlistId
        getPlaylistTracks(playlistId)
    }

    private fun getPlaylistTracks(playlistId: Int) {
        viewModelScope.launch {
            playlistsInteractor
                .getTracksInPlaylist(playlistId)
                .collect { tracks ->
                    processResult(tracks)
                }
        }
    }

    private fun processResult(tracks: List<Track>) {
        val playlist = playlistsInteractor.getPlaylistById(playlistId)
        if (tracks.isEmpty()) {
            renderState(PlaylistViewerState.EmptyTracks(playlist, 0))
        } else {
            var durationSum = 0
            tracks.forEach {
                durationSum = (durationSum + it.trackTimeMillis).toInt()
            }
            renderState(PlaylistViewerState.Content(playlist, tracks, durationSum))
        }
    }

    private fun renderState(state: PlaylistViewerState) {
        stateLiveData.postValue(state)
    }

    private fun DurationToStringConverter(durationSum: Int): String {
        val totalDurString = SimpleDateFormat("mm", Locale.getDefault()).format(durationSum)
        return  totalDurString + " " + context.resources.getQuantityString(R.plurals.plural_minutes, totalDurString.toInt())
    }

    fun getPlaylistDuration(): String {
        when (stateLiveData.value) {
            is PlaylistViewerState.Content ->
                return DurationToStringConverter((stateLiveData.value as PlaylistViewerState.Content).playlistDuration)
            else ->
                return DurationToStringConverter(0)
        }
    }

    fun getTracksAmountString(): String {
        val playlist = getPlaylist()
        val count = playlist.trackCount
        return (playlist.trackCount.toString() + " " + context.resources.getQuantityString(R.plurals.plural_tracks, count))
    }

    fun getPlaylist(): Playlist {
        when (stateLiveData.value) {
            is PlaylistViewerState.Content ->
                return  (stateLiveData.value as PlaylistViewerState.Content).playlist
            is PlaylistViewerState.EmptyTracks ->
                return  (stateLiveData.value as PlaylistViewerState.EmptyTracks).playlist
            else ->
                return Playlist()
        }
    }

    fun deleteTrack(trackId: Int, playlistId: Int) {
        if (stateLiveData.value is PlaylistViewerState.Content) {
            val plList = (stateLiveData.value as PlaylistViewerState.Content).playlist
            if (plList.trackCount == 1) renderState(PlaylistViewerState.EmptyTracks(plList,0))
        }
        playlistsInteractor.deleteTrackFromPlaylist(trackId, playlistId)
        getPlaylistTracks(playlistId)
    }

    fun deletePlaylist() {
        playlistsInteractor.deletePlaylist(playlistId)
    }

    fun makeSharingPlaylistMessage(): String {
        when (stateLiveData.value){
            is PlaylistViewerState.Content -> {
                val playlist = (stateLiveData.value as PlaylistViewerState.Content).playlist
                val tracks: List<Track> = (stateLiveData.value as PlaylistViewerState.Content).tracks
                var message = playlist.playlistTitle + "\n" + playlist.playlistDescription + "\n" +
                        getTracksAmountString() + "\n"
                var counter = 1
                tracks.forEach {
                    message += "${counter.toString()}. ${it.artistName} - ${it.trackName} (${
                        DurationToStringConverter(it.trackTimeMillis.toInt())})\n"
                    counter++
                }
                return message
            } else -> {
                return ""
            }
        }
    }

    fun sharePlaylist(message: String) {
        sharingInteractor.sharePlaylist(message)
    }
}
