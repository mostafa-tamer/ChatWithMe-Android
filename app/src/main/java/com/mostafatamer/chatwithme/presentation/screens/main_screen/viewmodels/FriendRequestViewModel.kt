package com.mostafatamer.chatwithme.presentation.screens.main_screen.viewmodels

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.mostafatamer.chatwithme.domain.usecase.FriendShipUseCase
import com.mostafatamer.chatwithme.domain.model.dto.dto.FriendRequestDto
import com.mostafatamer.chatwithme.presentation.viewmodels.abstract_view_models.StompLifecycleManager
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class FriendRequestViewModel @Inject constructor(
    private val friendShipUseCase: FriendShipUseCase,
) : ViewModel() {

    val friendRequests get() = friendShipUseCase.friendRequests

    var isLoading by mutableStateOf(false)

    var isThereNoFriendRequests by mutableStateOf(false)

    val stompLifecycleManager = StompLifecycleManager(friendShipUseCase.stompService)

    private fun notifyFriendRequests() {
        isThereNoFriendRequests = friendRequests.isEmpty()
    }

    fun observeFriendRequestsAndLoadFriends() {
        friendShipUseCase.observeFriendRequestsAndLoadFriends(
            onNewFriendRequest = { notifyFriendRequests() }) {
            isLoading = it
        }
    }

    fun acceptFriendRequest(
        senderUsername: String,
        onAcceptFriendRequest: (succeeded: Boolean) -> Unit,
    ) {
        friendShipUseCase.acceptFriendRequest(senderUsername) {
            onAcceptFriendRequest(it)
            notifyFriendRequests()
        }
    }

    fun sendFriendRequest(
        username: String, message: String, sendFriendRequest: (succeeded: Boolean) -> Unit,
    ) {
        friendShipUseCase.sendFriendRequest(username, message, sendFriendRequest)
    }

    fun reset() {
        friendShipUseCase.reset()
        isLoading = false
        isThereNoFriendRequests = false
    }

    fun removeFriendRequest(
        friendRequestDto: FriendRequestDto,
        onFriendRequestRemoved: (Boolean) -> Unit,
    ) {
        friendShipUseCase.removeFriendRequest(friendRequestDto) {
            onFriendRequestRemoved(it)
            notifyFriendRequests()
        }
    }
}