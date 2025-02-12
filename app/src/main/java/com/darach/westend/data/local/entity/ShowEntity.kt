package com.darach.westend.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.darach.westend.domain.model.Show

@Entity(tableName = "shows")
data class ShowEntity(
    @PrimaryKey
    val title: String,
    val venue: String,
    val category: String,
    val bookingUntil: String,
    val description: String,
    val runningTime: Int,
    val minimumAge: Int,
    val priceRange: String,
    val showTimes: String,
    val status: String,
    val imageUrl: String,
    val showType: String,
    val rating: Double,
    val numberOfRatings: String,
    val awards: String,
    val endDate: String?,
    val specialOffer: Boolean,
    val lastUpdated: Long = System.currentTimeMillis(),
    val latitude: Double,
    val longitude: Double,
    val isSaved: Boolean = false,
    val savedAt: Long? = null
) {
    fun toShow(): Show {
        return Show(
            title = title,
            venue = venue,
            category = category,
            bookingUntil = bookingUntil,
            description = description,
            runningTime = runningTime,
            minimumAge = minimumAge,
            priceRange = priceRange,
            showTimes = showTimes,
            status = status,
            imageUrl = imageUrl,
            showType = showType,
            rating = rating,
            numberOfRatings = numberOfRatings,
            awards = if (awards.isBlank()) emptyList() else awards.split(",").map { it.trim() },
            endDate = endDate,
            specialOffer = specialOffer,
            latitude = latitude,
            longitude = longitude,
            isSaved = isSaved,
            savedAt = savedAt
        )
    }

    companion object {
        fun fromShow(show: Show): ShowEntity {
            return ShowEntity(
                title = show.title,
                venue = show.venue,
                category = show.category,
                bookingUntil = show.bookingUntil,
                description = show.description,
                runningTime = show.runningTime,
                minimumAge = show.minimumAge,
                priceRange = show.priceRange,
                showTimes = show.showTimes,
                status = show.status,
                imageUrl = show.imageUrl,
                showType = show.showType,
                rating = show.rating,
                numberOfRatings = show.numberOfRatings,
                awards = show.awards.joinToString(","),
                endDate = show.endDate,
                specialOffer = show.specialOffer,
                latitude = show.latitude,
                longitude = show.longitude,
                isSaved = show.isSaved,
                savedAt = show.savedAt
            )
        }
    }
}