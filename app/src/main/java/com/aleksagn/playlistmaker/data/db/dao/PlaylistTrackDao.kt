package com.aleksagn.playlistmaker.data.db.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.aleksagn.playlistmaker.data.db.entity.PlaylistTrackEntity

@Dao
interface PlaylistTrackDao {
    @Insert(entity = PlaylistTrackEntity::class, onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPlaylistTrack(playlistTrackEntity: PlaylistTrackEntity)

    @Delete(entity = PlaylistTrackEntity::class)
    suspend fun deletePlaylistTrack(playlistTrackEntity: PlaylistTrackEntity)

    @Query("SELECT * FROM playlist_track WHERE playlistId = :playlistId")
    suspend fun getItemsByPlaylistId(playlistId: Int): List<PlaylistTrackEntity?>

    @Query("DELETE FROM playlist_track WHERE playlistId = :playlistId")
    suspend fun deleteItemsByPlaylistId(playlistId: Int)

    @Query("SELECT * FROM playlist_track WHERE trackId = :id")
    suspend fun getItemsByTrackId(id: Int): List<PlaylistTrackEntity?>

    @Query("SELECT * FROM playlist_track WHERE trackId = :trackId AND playlistId = :playlistId")
    suspend fun getItemByPlaylistIdAndTrackId(trackId: Int, playlistId: Int): PlaylistTrackEntity?
}