package com.example.santabarbaramobile.ui.components

import java.time.Instant
import java.time.Duration
import java.time.format.DateTimeParseException

fun canEditReview(createdAt: String): Boolean {
    return try {
        val createdDate = Instant.parse(createdAt)
        val now = Instant.now()
        val difference = Duration.between(createdDate, now)

        difference.toMinutes() < 30
    } catch (e: Exception) {
        false
    }
}