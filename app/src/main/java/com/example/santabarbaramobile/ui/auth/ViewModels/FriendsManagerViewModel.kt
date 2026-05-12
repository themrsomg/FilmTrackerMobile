package com.example.santabarbaramobile.ui.auth.ViewModels

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.santabarbaramobile.data.model.UserDto
import com.example.santabarbaramobile.data.repository.FriendsRepository
import com.example.santabarbaramobile.data.repository.UserRepository
import com.example.santabarbaramobile.data.security.TokenManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.collections.emptyList

data class FriendUIItem(val id: Int, val user: UserDto)
data class RequestUIItem(val requestId: Int, val user: UserDto)

data class FriendsManagerState(
    val isLoading: Boolean = true,
    val friends: List<FriendUIItem> = emptyList(),
    val incomingRequests: List<RequestUIItem> = emptyList(),
    val outgoingRequests: List<RequestUIItem> = emptyList(),
    val error: String? = null
)

@HiltViewModel
class FriendsManagerViewModel @Inject constructor(
    private val friendsRepository: FriendsRepository,
    private val userRepository: UserRepository,
    private val tokenManager: TokenManager
) : ViewModel() {

    var uiState by mutableStateOf(FriendsManagerState())
        private set

    fun loadData() {
        viewModelScope.launch {
            uiState = uiState.copy(isLoading = true, error = null)

            val token = tokenManager.getToken() ?: return@launch
            val profileRes = userRepository.getUserProfile("Bearer $token").getOrNull()
            val myAuthId = profileRes?.authId ?: return@launch

            val friendsDeferred = async { friendsRepository.getFriends(myAuthId, 1) }
            val incomingDeferred = async { friendsRepository.getIncomingRequests(1) }
            val outgoingDeferred = async { friendsRepository.getOutgoingRequests(1) }

            val rawFriends = friendsDeferred.await().getOrNull()?.data ?: emptyList()
            val rawIncoming = incomingDeferred.await().getOrNull()?.data ?: emptyList()
            val rawOutgoing = outgoingDeferred.await().getOrNull()?.data ?: emptyList()

            val populatedFriends = rawFriends.map { item ->
                async {
                    userRepository.getUserById(item.friendAuthId).getOrNull()?.let { user ->
                        FriendUIItem(item.id, user)
                    }
                }
            }.awaitAll().filterNotNull()

            val populatedIncoming = rawIncoming.map { req ->
                async {
                    userRepository.getUserById(req.requesterAuthId).getOrNull()?.let { user ->
                        RequestUIItem(req.id, user)
                    }
                }
            }.awaitAll().filterNotNull()

            val populatedOutgoing = rawOutgoing.map { req ->
                async {
                    userRepository.getUserById(req.receiverAuthId).getOrNull()?.let { user ->
                        RequestUIItem(req.id, user)
                    }
                }
            }.awaitAll().filterNotNull()

            uiState = uiState.copy(
                friends = populatedFriends,
                incomingRequests = populatedIncoming,
                outgoingRequests = populatedOutgoing,
                isLoading = false
            )
        }
    }

    fun acceptRequest(requestId: Int) = viewModelScope.launch {
        friendsRepository.acceptFriendRequest(requestId).onSuccess { loadData() }
    }

    fun rejectRequest(requestId: Int) = viewModelScope.launch {
        friendsRepository.rejectFriendRequest(requestId).onSuccess { loadData() }
    }

    fun cancelRequest(requestId: Int) = viewModelScope.launch {
        friendsRepository.cancelFriendRequest(requestId).onSuccess { loadData() }
    }
}