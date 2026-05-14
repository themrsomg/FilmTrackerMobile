package com.example.santabarbaramobile.data.repository

import com.example.santabarbaramobile.data.model.NotificationDto
import com.example.santabarbaramobile.data.remote.notifications.NotificationsApi
import com.example.santabarbaramobile.data.security.TokenManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NotificationsRepository @Inject constructor(
    private val api: NotificationsApi,
    private val tokenManager: TokenManager
) {
    suspend fun getNotifications(page: Int = 1): Result<List<NotificationDto>> = withContext(Dispatchers.IO) {
        try {
            val token = "Bearer ${tokenManager.getToken()}"
            val response = api.getNotifications(token, page)
            if (response.isSuccessful) {
                Result.success(response.body()?.notifications ?: emptyList())
            } else {
                Result.failure(Exception("Error al cargar notificaciones"))
            }
        } catch (e: Exception) { Result.failure(e) }
    }

    suspend fun getUnreadCount(): Result<Int> = withContext(Dispatchers.IO) {
        try {
            val token = "Bearer ${tokenManager.getToken()}"
            val response = api.getUnreadCount(token)
            if (response.isSuccessful) {
                Result.success(response.body()?.unreadCount ?: 0)
            } else {
                Result.failure(Exception("Error al obtener conteo"))
            }
        } catch (e: Exception) { Result.failure(e) }
    }

    suspend fun markAsRead(notificationId: Int): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            val token = "Bearer ${tokenManager.getToken()}"
            val response = api.markAsRead(token, notificationId)
            if (response.isSuccessful) Result.success(Unit) else Result.failure(Exception("Error"))
        } catch (e: Exception) { Result.failure(e) }
    }

    suspend fun markAllAsRead(): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            val token = "Bearer ${tokenManager.getToken()}"
            val response = api.markAllAsRead(token)
            if (response.isSuccessful) Result.success(Unit) else Result.failure(Exception("Error"))
        } catch (e: Exception) { Result.failure(e) }
    }
}