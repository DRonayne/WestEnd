package com.darach.westend.domain.model

data class Show(
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
    val rating: Double = 0.0,
    val numberOfRatings: String = "",
    val awards: List<String> = emptyList(),
    val endDate: String? = null,
    val specialOffer: Boolean = false,
    val latitude: Double = 51.5074,
    val longitude: Double = -0.1278,
    val isSaved: Boolean = false,
    val savedAt: Long? = null
)