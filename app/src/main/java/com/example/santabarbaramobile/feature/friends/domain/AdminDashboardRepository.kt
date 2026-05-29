package com.example.santabarbaramobile.feature.friends.domain

import com.example.santabarbaramobile.core.security.TokenManager
import com.example.santabarbaramobile.feature.profile.domain.*
import com.example.santabarbaramobile.feature.profile.data.ModerationApi
import com.example.santabarbaramobile.feature.reviews.data.ReviewsApi
import com.example.santabarbaramobile.feature.auth.data.AuthApi
import com.example.santabarbaramobile.feature.profile.data.UsersApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.time.ZoneOffset
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AdminDashboardRepository @Inject constructor(
    private val authApi: AuthApi,
    private val reviewsApi: ReviewsApi,
    private val moderationApi: ModerationApi,
    private val usersApi: UsersApi,
    private val tokenManager: TokenManager
) {
    suspend fun getAuthStats(): Result<AuthStatsDto> = withContext(Dispatchers.IO) {
        try {
            val res = authApi.getAuthStats("Bearer ${tokenManager.getToken()}")
            if (res.isSuccessful && res.body()?.data != null) Result.success(res.body()!!.data!!)
                else Result.failure(Exception())
        } catch (e: Exception) { Result.failure(e) }
    }

    suspend fun getReviewStats(): Result<ReviewStatsDto> = withContext(Dispatchers.IO) {
        try {
            val res = reviewsApi.getReviewStats("Bearer ${tokenManager.getToken()}")
            if (res.isSuccessful && res.body()?.data != null) Result.success(res.body()!!.data!!)
                else Result.failure(Exception())
        } catch (e: Exception) { Result.failure(e) }
    }

    suspend fun getModerationStats(): Result<ModerationStatsDto> = withContext(Dispatchers.IO) {
        try {
            val res = moderationApi.getModerationStats("Bearer ${tokenManager.getToken()}")
            if (res.isSuccessful && res.body()?.data != null) Result.success(res.body()!!.data!!)
                else Result.failure(Exception())
        } catch (e: Exception) { Result.failure(e) }
    }

    suspend fun searchUsers(query: String): Result<List<UserDto>> = withContext(Dispatchers.IO) {
        try {
            val res = usersApi.searchUsersAdmin("Bearer ${tokenManager.getToken()}", query)
            if (res.isSuccessful && res.body()?.data != null) {
                Result.success(res.body()!!.data!!)
            } else {
                Result.failure(Exception("No se encontraron usuarios"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getAdminUserDetails(authId: String): Result<AdminUserDetailDto> = withContext(Dispatchers.IO) {
        try {
            val res = usersApi.getAdminUserDetails("Bearer ${tokenManager.getToken()}", authId)
            if (res.isSuccessful && res.body()?.data != null) Result.success(res.body()!!.data!!)
                else Result.failure(Exception("Error ${res.code()}"))
        } catch (e: Exception) { Result.failure(e) }
    }

    suspend fun getUserById(authId: String): Result<UserDto> = withContext(Dispatchers.IO) {
        try {
            val res = usersApi.getUserById(authId)
            if (res.isSuccessful && res.body()?.data != null) Result.success(res.body()!!.data)
                else Result.failure(Exception())
        } catch (e: Exception) { Result.failure(e) }
    }

    suspend fun removeProfilePhoto(authId: String): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            val res = usersApi.removeProfilePhotoDirectly("Bearer ${tokenManager.getToken()}", authId)
            if (res.isSuccessful) Result.success(Unit)
                else Result.failure(Exception())
        } catch (e: Exception) { Result.failure(e) }
    }

    suspend fun getAccountStatus(authId: String): Result<AccountStatusDto> = withContext(Dispatchers.IO) {
        try {
            val res = authApi.getAccountStatus("Bearer ${tokenManager.getToken()}", authId)
            if (res.isSuccessful && res.body() != null) Result.success(res.body()!!)
                else Result.failure(Exception())
        } catch (e: Exception) { Result.failure(e) }
    }

    suspend fun banUser(authId: String, reason: String): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            val res = authApi.banUser("Bearer ${tokenManager.getToken()}", authId, BanRequestDto(reason))
            if (res.isSuccessful) Result.success(Unit)
                else Result.failure(Exception())
        } catch (e: Exception) { Result.failure(e) }
    }

    suspend fun unbanUser(authId: String): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            val res = authApi.unbanUser("Bearer ${tokenManager.getToken()}", authId)
            if (res.isSuccessful) Result.success(Unit)
                else Result.failure(Exception())
        } catch (e: Exception) { Result.failure(e) }
    }

    suspend fun suspendUser(authId: String, durationBackend: String, reason: String): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            val days = when (durationBackend) {
                "1_DAY" -> 1L
                "3_DAYS" -> 3L
                "30_DAYS" -> 30L
                else -> 7L
            }
            val suspendedUntil = ZonedDateTime.now(ZoneOffset.UTC).plusDays(days).format(DateTimeFormatter.ISO_INSTANT)
            val res = authApi.suspendUser("Bearer ${tokenManager.getToken()}", authId, SuspendRequestDto(suspendedUntil, reason))
            if (res.isSuccessful) Result.success(Unit) else Result.failure(Exception())
        } catch (e: Exception) { Result.failure(e) }
    }
}