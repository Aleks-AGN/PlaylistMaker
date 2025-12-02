package com.aleksagn.playlistmaker.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.aleksagn.playlistmaker.data.db.dao.PlaylistDao
import com.aleksagn.playlistmaker.data.db.dao.PlaylistTrackDao
import com.aleksagn.playlistmaker.data.db.dao.TrackDao
import com.aleksagn.playlistmaker.data.db.dao.TrackInListDao
import com.aleksagn.playlistmaker.data.db.entity.PlaylistEntity
import com.aleksagn.playlistmaker.data.db.entity.PlaylistTrackEntity
import com.aleksagn.playlistmaker.data.db.entity.TrackEntity
import com.aleksagn.playlistmaker.data.db.entity.TrackInListEntity

@Database(version = 1, entities = [TrackEntity::class, PlaylistEntity::class, PlaylistTrackEntity::class, TrackInListEntity::class])
abstract class AppDatabase : RoomDatabase(){

    abstract fun trackDao(): TrackDao
    abstract fun playlistDao(): PlaylistDao
    abstract fun playlistTrackDao(): PlaylistTrackDao
    abstract fun trackInListDao(): TrackInListDao
}
