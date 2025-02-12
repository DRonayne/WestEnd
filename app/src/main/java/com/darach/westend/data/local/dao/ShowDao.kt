package com.darach.westend.data.local.dao

import androidx.room.*
import com.darach.westend.data.local.entity.ShowEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ShowDao {
    @Query("SELECT * FROM shows")
    fun getAllShows(): Flow<List<ShowEntity>>

    @Query("SELECT * FROM shows WHERE status = :status")
    fun getShowsByStatus(status: String): Flow<List<ShowEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertShows(shows: List<ShowEntity>)

    @Query("DELETE FROM shows")
    suspend fun deleteAllShows()

    @Query("SELECT * FROM shows WHERE title = :title")
    suspend fun getShowByTitle(title: String): ShowEntity?

    @Query("SELECT MAX(lastUpdated) FROM shows")
    suspend fun getLastUpdateTime(): Long?

    @Query("SELECT * FROM shows WHERE isSaved = 1 ORDER BY savedAt DESC")
    fun getSavedShows(): Flow<List<ShowEntity>>

    @Query("UPDATE shows SET isSaved = :isSaved, savedAt = :timestamp WHERE title = :title")
    suspend fun updateSavedStatus(title: String, isSaved: Boolean, timestamp: Long?)

    @Query("SELECT isSaved FROM shows WHERE title = :title")
    suspend fun isShowSaved(title: String): Boolean
}