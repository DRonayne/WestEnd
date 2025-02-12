package com.darach.westend.domain.repository

import com.darach.westend.data.local.dao.ShowDao
import com.darach.westend.data.local.entity.ShowEntity
import com.darach.westend.data.remote.api.ShowApi
import com.darach.westend.domain.model.Show
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.util.concurrent.TimeUnit
import javax.inject.Inject

const val USER_CONTENT_KEY =
    "UCcWLlw1yty9-Mj5TOhx-OO8r5IpX5iXkJA7WgeibmGzsHVY5YyHKSyihVYcjERQU5vqGCYIzja0UOL8-J8SQcjMF2kO5tqtm5_BxDlH2jW0nuo2oDemN9CCS2h10ox_1xSncGQajx_ryfhECjZEnE0eveBLOFVTdWL8irBlxsKxc_86T6uJwuIbCtSp8zpDW_6yKopCcy-PazfnZY5SHxW1Jfr7BL8VFBDeUZ6IThyMNOJV5SiYKtz9Jw9Md8uu"
const val LIB_KEY = "MVWxaTfvrJK4FBWMiFTfaXzWvAevbO5Q_"

class ShowRepository @Inject constructor(
    private val api: ShowApi,
    private val dao: ShowDao
) {
    fun getShows(): Flow<List<Show>> {
        return dao.getAllShows().map { entities ->
            entities.map { it.toShow() }
        }
    }

    suspend fun refreshShows() {
        try {
            // Check if database is empty or needs to be refreshed
            val lastUpdate = dao.getLastUpdateTime() ?: 0
            val currentTime = System.currentTimeMillis()
            val dbIsEmpty = lastUpdate == 0L
            val shouldRefresh = dbIsEmpty || currentTime - lastUpdate > TimeUnit.HOURS.toMillis(1)

            if (shouldRefresh) {
                val remoteShows = api.getShows(USER_CONTENT_KEY, LIB_KEY).map { dto ->
                    ShowEntity.fromShow(
                        Show(
                            title = dto.title,
                            venue = dto.venue,
                            category = dto.category,
                            bookingUntil = dto.bookingUntil,
                            description = dto.description,
                            runningTime = dto.runningTime,
                            minimumAge = dto.minimumAge,
                            priceRange = dto.priceRange,
                            showTimes = dto.showTimes,
                            status = dto.status,
                            imageUrl = dto.imageUrl,
                            showType = dto.showType,
                            rating = dto.rating,
                            numberOfRatings = dto.numberOfRatings,
                            awards = dto.awards,
                            endDate = dto.endDate,
                            specialOffer = dto.specialOffer,
                            latitude = dto.latitude,
                            longitude = dto.longitude
                        )

                    )
                }
                dao.deleteAllShows()
                dao.insertShows(remoteShows)
            }
        } catch (e: Exception) {
            if (dao.getLastUpdateTime() == 0L) {
                throw Exception("Unable to load shows: ${e.message}")
            }
        }
    }

    fun getSavedShows(): Flow<List<Show>> {
        return dao.getSavedShows().map { entities ->
            entities.map { it.toShow() }
        }
    }

    suspend fun toggleSaveShow(title: String) {
        val currentlySaved = dao.isShowSaved(title)

        val newSaveState = !currentlySaved
        val timestamp = if (newSaveState) System.currentTimeMillis() else null

        try {
            dao.updateSavedStatus(
                title = title,
                isSaved = newSaveState,
                timestamp = timestamp
            )
        } catch (e: Exception) {
            println("Error updating save state: ${e.message}")
        }
    }

    suspend fun isShowSaved(title: String): Boolean {
        return dao.isShowSaved(title)
    }
}