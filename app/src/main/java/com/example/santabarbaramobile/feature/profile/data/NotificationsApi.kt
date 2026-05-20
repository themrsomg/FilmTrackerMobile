package com.example.santabarbaramobile.feature.profile.data

import com.example.santabarbaramobile.feature.profile.domain.NotificationPaginationResponse
import com.example.santabarbaramobile.feature.profile.domain.UnreadCountResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

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