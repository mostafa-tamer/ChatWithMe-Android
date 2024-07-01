package com.mostafatamer.chatwithme.domain.usecase

import androidx.compose.runtime.mutableStateListOf
import com.mostafatamer.chatwithme.data.repository.realtime.StompRepository
import com.mostafatamer.chatwithme.domain.model.dto.dto.Chat
import com.mostafatamer.chatwithme.domain.model.dto.dto.FriendRequestDto
import com.mostafatamer.chatwithme.domain.model.dto.dto.SendFriendRequestDto
import com.mostafatamer.chatwithme.domain.model.dto.dto.UserDto
import com.mostafatamer.chatwithme.data.repository.restful.FriendshipRepository

class FriendShipUseCase(
    private val friendshipRepository: FriendshipRepository,
    private val stompRepository: StompRepository,
) {
    val stompService get() = stompRepository.service

    val friendRequests = mutableStateListOf<FriendRequestDto>()

    fun removeFriend(friend: UserDto, onFriendRemoved: (Boolean) -> Unit) {
        friendshipRepository.removeFriend(friend.username)
            .setOnSuccess {
                onFriendRemoved(it.apiError == null)
            }.execute()
    }

    fun removeFriendRequest(
        friendRequestDto: FriendRequestDto,
        onFriendRequestRemoved: (Boolean) -> Unit,
    ) {
        friendshipRepository.removeFriendRequest(friendRequestDto.sender.username)
            .setOnSuccess {
                if (it.data != null) friendRequests.remove(friendRequestDto)
                onFriendRequestRemoved(it.apiError == null)
            }.execute()
    }

    fun acceptFriendRequest(
        senderUsername: String,
        onAcceptFriendRequest: (succeeded: Boolean) -> Unit,
    ) {
        friendshipRepository.acceptFriendRequest(senderUsername)
            .setOnSuccess {
                if (it != null) {
                    friendRequests.removeIf { friendRequest ->
                        friendRequest.sender.username == senderUsername
                    }
                }

                onAcceptFriendRequest(it != null)
            }.execute()
    }

    private fun loadFriends(
        onNewFriendRequest: () -> Unit,
        onLoadingStateChange: (Boolean) -> Unit,
    ) {
        friendshipRepository.getFriendRequests()
            .setOnSuccess { apiResponse ->
                apiResponse.data?.let { friendRequests ->
                    this.friendRequests.clear()
                    this.friendRequests.addAll(friendRequests)
                    onNewFriendRequest()
                }
            }.setLoadingObserver {
                onLoadingStateChange(it)
            }.execute()
    }

    fun observeFriendRequestsAndLoadFriends(
        onNewFriendRequest: () -> Unit,
        onLoadingStateChange: (Boolean) -> Unit,
    ) {
        stompRepository.observeFriendRequestsAndLoadFriends(onSubscribe = {
            loadFriends(onNewFriendRequest, onLoadingStateChange)
        }) { friendRequestDto ->
            if (!friendRequests.contains(friendRequestDto)) {
                friendRequests.add(friendRequestDto)
                onNewFriendRequest()
            }
        }
    }

    fun sendFriendRequest(
        username: String,
        message: String,
        sendFriendRequest: (succeeded: Boolean) -> Unit,
    ) {
        friendshipRepository.sendFriendRequest(
            SendFriendRequestDto(
                receiverUsername = username,
                message = message
            )
        ).setOnSuccess {
            if (it != null) {
                sendFriendRequest(it.data != null)
            }
        }.execute()
    }

    fun observeFriendRemovedMe(onFriendRemovedMe: (Chat) -> Unit) {
        stompRepository.observeFriendRemovedMe {
            onFriendRemovedMe(it)
        }
    }


    fun reset() {
        friendRequests.clear()
    }
}