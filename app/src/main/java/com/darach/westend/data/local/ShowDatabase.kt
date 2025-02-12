package com.darach.westend.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.darach.westend.data.local.dao.ShowDao
import com.darach.westend.data.local.entity.ShowEntity

@Database(
    entities = [ShowEntity::class],
    version = 1
)
abstract class ShowDatabase : RoomDatabase() {
    abstract val showDao: ShowDao

    companion object {
        const val DATABASE_NAME = "shows_db"
    }
}