package com.example.santabarbaramobile.feature.profile.domain

data class AuthStatsDto(
    val totalUsers: Int? = 0,
    val newUsers: Map<String, Int>? = null,
    val byStatus: Map<String, Int>? = null,
    val byRole: Map<String, Int>? = null
)

data class ReviewStatsDto(
    val totals: Map<String, Double>? = null
)

data class ModerationStatsDto(
    val totalReports: Int? = 0,
    val pendingReports: Int? = 0,
    val resolvedReports: Int? = 0
)

data class AdminStatsResponse<T>(
    val message: String?,
    val data: T?
)