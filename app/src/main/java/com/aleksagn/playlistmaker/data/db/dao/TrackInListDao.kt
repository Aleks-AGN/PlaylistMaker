package com.aleksagn.playlistmaker.data.db.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.aleksagn.playlistmaker.data.db.entity.TrackInListEntity

@Dao
interface TrackInListDao {
    @Insert(entity = TrackInListEntity::class, onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTrack(trackEntity: TrackInListEntity)

    @Delete(entity = TrackInListEntity::class)
    suspend fun deleteTrack(track: TrackInListEntity)

    @Query("DELETE  FROM playlists_tracks WHERE trackId = :trackId ")
    suspend fun deleteTrackById(trackId: Int)

    @Query("SELECT * FROM playlists_tracks WHERE trackId = :trackId ")
    suspend fun getTrackById(trackId: Int): TrackInListEntity?

    @Query("SELECT * FROM playlists_tracks WHERE trackId IN (:trackIds)")
    suspend fun getTracksByIds(trackIds: List<Int>): List<TrackInListEntity>
}