package com.darach.westend.utils

import java.text.SimpleDateFormat
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.temporal.ChronoUnit
import java.util.Locale
import java.util.TimeZone

fun calculateEndsInText(endDateStr: String): String {
    return try {
        val instant = Instant.parse(endDateStr)
        calculateDifferenceText(
            LocalDateTime.ofInstant(instant, ZoneId.systemDefault()),
            LocalDateTime.now()
        )
    } catch (e: Exception) {
        parseWithAlternativeFormats(endDateStr)
    }
}

private fun calculateDifferenceText(endDate: LocalDateTime, currentDate: LocalDateTime): String {
    val diffInDays = ChronoUnit.DAYS.between(currentDate, endDate)

    return when {
        diffInDays < 0 -> "Ended"
        diffInDays == 0L -> "Ends today"
        diffInDays == 1L -> "Ends tomorrow"
        diffInDays < 7 -> "Ends in $diffInDays days"
        diffInDays < 30 -> {
            val weeks = diffInDays / 7
            "Ends in ${if (weeks == 1L) "1 week" else "$weeks weeks"}"
        }

        else -> {
            val months = diffInDays / 30
            "Ends in ${if (months == 1L) "1 month" else "$months months"}"
        }
    }
}

private fun parseWithAlternativeFormats(endDateStr: String): String {
    val formats = arrayOf(
        "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'",
        "yyyy-MM-dd'T'HH:mm:ss'Z'",
        "dd/MM/yyyy",
        "yyyy-MM-dd"
    )

    val currentDate = System.currentTimeMillis()

    formats.forEach { format ->
        runCatching {
            val dateFormat = SimpleDateFormat(format, Locale.US).apply {
                timeZone = TimeZone.getTimeZone("UTC")
            }

            dateFormat.parse(endDateStr)?.time?.let { date ->
                val diffInDays = (date - currentDate) / (1000 * 60 * 60 * 24)
                return when {
                    diffInDays < 0 -> "Ended"
                    diffInDays == 0L -> "Ends today"
                    diffInDays == 1L -> "Ends tomorrow"
                    diffInDays < 7 -> "Ends in $diffInDays days"
                    diffInDays < 30 -> {
                        val weeks = diffInDays / 7
                        "Ends in ${if (weeks == 1L) "1 week" else "$weeks weeks"}"
                    }

                    else -> {
                        val months = diffInDays / 30
                        "Ends in ${if (months == 1L) "1 month" else "$months months"}"
                    }
                }
            }
        }
    }

    return ""
}