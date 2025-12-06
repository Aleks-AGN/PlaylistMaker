package com.aleksagn.playlistmaker.data.db.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.aleksagn.playlistmaker.data.db.entity.PlaylistEntity

@Dao
interface PlaylistDao {
    @Insert(entity = PlaylistEntity::class, onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPlaylist(playlistEntity: PlaylistEntity)

    @Delete(entity = PlaylistEntity::class)
    suspend fun deletePlaylist(playlist: PlaylistEntity)

    @Query("DELETE FROM playlists WHERE playlistId = :playlistId")
    suspend fun deletePlaylistById(playlistId: Int)

    @Query("SELECT * FROM playlists")
    suspend fun getPlaylists(): List<PlaylistEntity>

    @Query("SELECT * FROM playlists WHERE playlistId = :playlistId")
    suspend fun getPlaylistById(playlistId: Int): PlaylistEntity?

    @Query("UPDATE playlists SET trackList = :newValue WHERE playlistId = :playlistId")
    fun updateFieldTrackListById(playlistId: Int, newValue: String)

    @Query("UPDATE playlists SET trackCount = trackCount + 1 WHERE playlistId = :playlistId")
    suspend fun incrementFieldTrackCount(playlistId: Int)

    @Query("UPDATE playlists SET trackCount = trackCount - 1 WHERE playlistId = :playlistId")
    suspend fun decrementFieldTracksCount(playlistId: Int)

    @Query("UPDATE playlists SET playlistTitle = :playlistTitle, playlistDescription = :playlistDescription, playlistImageUri = :playlistImageUri WHERE playlistId = :playlistId")
    suspend fun updatePlaylist(playlistId: Int, playlistTitle: String, playlistDescription: String, playlistImageUri: String?)
}