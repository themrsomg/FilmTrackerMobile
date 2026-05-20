package com.example.santabarbaramobile.feature.friends.presentation

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.santabarbaramobile.feature.friends.domain.FriendsRepository
import com.example.santabarbaramobile.feature.friends.domain.RequestUIItem
import com.example.santabarbaramobile.feature.friends.domain.FriendUIItem
import com.example.santabarbaramobile.feature.profile.domain.UserRepository
import com.example.santabarbaramobile.core.security.TokenManager
import com.example.santabarbaramobile.feature.friends.domain.FriendsManagerState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.collections.emptyList

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