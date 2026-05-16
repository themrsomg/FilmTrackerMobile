package com.example.santabarbaramobile.data.repository

import com.example.santabarbaramobile.data.model.models.FriendPaginationResponse
import com.example.santabarbaramobile.data.model.models.FriendRequestPaginationResponse
import com.example.santabarbaramobile.data.model.models.FriendStatusResponse
import com.example.santabarbaramobile.data.model.models.SendFriendRequest
import com.example.santabarbaramobile.data.remote.friends.FriendsApi
import com.example.santabarbaramobile.data.security.TokenManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FriendsRepository @Inject constructor(
    private val api: FriendsApi,
    private val tokenManager: TokenManager
) {
    suspend fun getRelationshipStatus(otherAuthId: String): Result<FriendStatusResponse> = withContext(Dispatchers.IO) {
        try {
            val token = "Bearer ${tokenManager.getToken()}"
            val response = api.getRelationshipStatus(token, otherAuthId)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.success(FriendStatusResponse("NONE", null))
            }
        } catch (e: Exception) {
            Result.success(FriendStatusResponse("NONE", null))
        }
    }

    suspend fun sendFriendRequest(receiverAuthId: String): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            val token = "Bearer ${tokenManager.getToken()}"
            val response = api.sendFriendRequest(token, SendFriendRequest(receiverAuthId))
            if (response.isSuccessful) Result.success(Unit)
            else Result.failure(Exception("Error al enviar solicitud"))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun removeFriend(friendAuthId: String): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            val token = "Bearer ${tokenManager.getToken()}"
            val response = api.removeFriend(token, friendAuthId)
            if (response.isSuccessful) Result.success(Unit)
            else Result.failure(Exception("Error al eliminar amigo"))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getFriends(authId: String, page: Int = 1): Result<FriendPaginationResponse> = withContext(Dispatchers.IO) {
        try {
            val token = "Bearer ${tokenManager.getToken()}"
            val response = api.getFriends(token, authId, page)
            if (response.isSuccessful && response.body() != null) Result.success(response.body()!!)
            else Result.failure(Exception("Error al cargar amigos"))
        } catch (e: Exception) { Result.failure(e) }
    }

    suspend fun getIncomingRequests(page: Int = 1): Result<FriendRequestPaginationResponse> = withContext(Dispatchers.IO) {
        try {
            val token = "Bearer ${tokenManager.getToken()}"
            val response = api.getIncomingRequests(token, page)
            if (response.isSuccessful && response.body() != null) Result.success(response.body()!!)
            else Result.failure(Exception("Error al cargar solicitudes"))
        } catch (e: Exception) { Result.failure(e) }
    }

    suspend fun getOutgoingRequests(page: Int = 1): Result<FriendRequestPaginationResponse> = withContext(Dispatchers.IO) {
        try {
            val token = "Bearer ${tokenManager.getToken()}"
            val response = api.getOutgoingRequests(token, page)
            if (response.isSuccessful && response.body() != null) Result.success(response.body()!!)
            else Result.failure(Exception("Error al cargar solicitudes"))
        } catch (e: Exception) { Result.failure(e) }
    }

    suspend fun acceptFriendRequest(requestId: Int): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            val token = "Bearer ${tokenManager.getToken()}"
            val response = api.acceptFriendRequest(token, requestId)
            if (response.isSuccessful) Result.success(Unit) else Result.failure(Exception("Error al aceptar"))
        } catch (e: Exception) { Result.failure(e) }
    }

    suspend fun rejectFriendRequest(requestId: Int): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            val token = "Bearer ${tokenManager.getToken()}"
            val response = api.rejectFriendRequest(token, requestId)
            if (response.isSuccessful) Result.success(Unit) else Result.failure(Exception("Error al rechazar"))
        } catch (e: Exception) { Result.failure(e) }
    }

    suspend fun cancelFriendRequest(requestId: Int): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            val token = "Bearer ${tokenManager.getToken()}"
            val response = api.cancelFriendRequest(token, requestId)
            if (response.isSuccessful) Result.success(Unit) else Result.failure(Exception("Error al cancelar"))
        } catch (e: Exception) { Result.failure(e) }
    }
}