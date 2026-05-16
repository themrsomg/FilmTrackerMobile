package com.example.santabarbaramobile.data.remote.notifications

import com.example.santabarbaramobile.data.model.dtos.NotificationPaginationResponse
import com.example.santabarbaramobile.data.model.dtos.UnreadCountResponse
import retrofit2.Response
import retrofit2.http.*

interface NotificationsApi {

    @GET("api/notifications")
    suspend fun getNotifications(
        @Header("Authorization") token: String,
        @Query("page") page: Int = 1
    ): Response<NotificationPaginationResponse>

    @GET("api/notifications/unread-count")
    suspend fun getUnreadCount(
        @Header("Authorization") token: String
    ): Response<UnreadCountResponse>

    @PUT("api/notifications/{id}/read")
    suspend fun markAsRead(
        @Header("Authorization") token: String,
        @Path("id") notificationId: Int
    ): Response<Unit>

    @PUT("api/notifications/read-all")
    suspend fun markAllAsRead(
        @Header("Authorization") token: String
    ): Response<Unit>
}