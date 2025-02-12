package com.darach.westend.data.remote.dto

import com.google.gson.annotations.SerializedName

data class ShowDto(
    @SerializedName("Title") val title: String,
    @SerializedName("Venue") val venue: String,
    @SerializedName("Category") val category: String,
    @SerializedName("BookingUntil") val bookingUntil: String,
    @SerializedName("Description") val description: String,
    @SerializedName("RunningTime") val runningTime: Int,
    @SerializedName("MinimumAge") val minimumAge: Int,
    @SerializedName("PriceRange") val priceRange: String,
    @SerializedName("ShowTimes") val showTimes: String,
    @SerializedName("Status") val status: String,
    @SerializedName("ImageUrl") val imageUrl: String,
    @SerializedName("ShowType") val showType: String,
    @SerializedName("Rating") val rating: Double = 0.0,
    @SerializedName("NumberOfRatings") val numberOfRatings: String = "0",
    @SerializedName("Awards") val awardsString: String = "",
    @SerializedName("EndDate") val endDate: String? = null,
    @SerializedName("SpecialOffer") val specialOffer: Boolean = false,
    @SerializedName("Latitude") val latitude: Double = 51.5074, // Default to London
    @SerializedName("Longitude") val longitude: Double = -0.1278
) {
    val awards: List<String>
        get() = if (awardsString.isBlank()) {
            emptyList()
        } else {
            awardsString.split(",").map { it.trim() }
        }
}