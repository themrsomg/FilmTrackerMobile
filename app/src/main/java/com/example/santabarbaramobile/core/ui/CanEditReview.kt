package com.example.santabarbaramobile.core.ui

import java.time.Instant
import java.time.Duration

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