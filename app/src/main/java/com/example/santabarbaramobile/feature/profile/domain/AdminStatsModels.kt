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

data class AdminDashboardState(
    val isLoading: Boolean = true,
    val isReportsLoading: Boolean = false,
    val isSearching: Boolean = false,
    val authStats: AuthStatsDto? = null,
    val reviewStats: ReviewStatsDto? = null,
    val modStats: ModerationStatsDto? = null,
    val reportsList: List<AdminReportDto> = emptyList(),
    val searchResults: List<UserDto> = emptyList(),
    val userDetailsMap: Map<String, UserDetailData> = emptyMap(),
    val currentFilter: String = "PENDING",
    val errorMessage: String? = null,
    val successMessage: String? = null
)

data class AdminUserDetailDto(
    val name: String?,
    val email: String?,
    val isEmailVerified: Boolean?,
    val role: String?,
    val createdAt: String?,
    val profileImage: String?
)

data class UserDetailData(
    val details: AdminUserDetailDto?,
    val status: AccountStatusDto?
)