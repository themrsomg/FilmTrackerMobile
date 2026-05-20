package com.example.santabarbaramobile.feature.friends.data

import com.example.santabarbaramobile.feature.friends.domain.FriendPaginationResponse
import com.example.santabarbaramobile.feature.friends.domain.FriendRequestPaginationResponse
import com.example.santabarbaramobile.feature.friends.domain.FriendStatusResponse
import com.example.santabarbaramobile.feature.friends.domain.FriendsSummaryDto
import com.example.santabarbaramobile.feature.friends.domain.SendFriendRequest
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

interface FriendsApi {

    @GET("api/friends/user/{authId}/summary")
    suspend fun getUserSummary(
        @Header("Authorization") token: String,
        @Path("authId") authId: String
    ): Response<FriendsSummaryDto>

    @GET("api/friends/user/{authId}")
    suspend fun getFriends(
        @Header("Authorization") token: String,
        @Path("authId") authId: String,
        @Query("page") page: Int = 1
    ): Response<FriendPaginationResponse>

    @GET("api/friends/{otherAuthId}/status")
    suspend fun getRelationshipStatus(
        @Header("Authorization") token: String,
        @Path("otherAuthId") otherAuthId: String
    ): Response<FriendStatusResponse>

    @POST("api/friends/requests")
    suspend fun sendFriendRequest(
        @Header("Authorization") token: String,
        @Body request: SendFriendRequest
    ): Response<Unit>

    @DELETE("api/friends/{friendAuthId}")
    suspend fun removeFriend(
        @Header("Authorization") token: String,
        @Path("friendAuthId") friendAuthId: String
    ): Response<Unit>

    @GET("api/friends/requests/incoming")
    suspend fun getIncomingRequests(
        @Header("Authorization") token: String,
        @Query("page") page: Int = 1
    ): Response<FriendRequestPaginationResponse>

    @GET("api/friends/requests/outgoing")
    suspend fun getOutgoingRequests(
        @Header("Authorization") token: String,
        @Query("page") page: Int = 1
    ): Response<FriendRequestPaginationResponse>

    @PUT("api/friends/requests/{requestId}/accept")
    suspend fun acceptFriendRequest(
        @Header("Authorization") token: String,
        @Path("requestId") requestId: Int
    ): Response<Unit>

    @PUT("api/friends/requests/{requestId}/reject")
    suspend fun rejectFriendRequest(
        @Header("Authorization") token: String,
        @Path("requestId") requestId: Int
    ): Response<Unit>

    @DELETE("api/friends/requests/{requestId}")
    suspend fun cancelFriendRequest(
        @Header("Authorization") token: String,
        @Path("requestId") requestId: Int
    ): Response<Unit>
}