package com.aleksagn.playlistmaker.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.aleksagn.playlistmaker.data.db.dao.TrackDao
import com.aleksagn.playlistmaker.data.db.entity.TrackEntity

@Database(version = 1, entities = [TrackEntity::class])
abstract class AppDatabase : RoomDatabase(){

    abstract fun trackDao(): TrackDao
}
