package com.mostafatamer.chatwithme.viewModels

import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import com.mostafatamer.chatwithme.enumeration.WebSocketPaths
import com.mostafatamer.chatwithme.network.entity.dto.FriendRequestDto
import com.mostafatamer.chatwithme.network.entity.dto.SendFriendRequestDto
import com.mostafatamer.chatwithme.network.repository.FriendshipRepository
import com.mostafatamer.chatwithme.services.StompService
import com.mostafatamer.chatwithme.static.AppUser
import com.mostafatamer.chatwithme.static.JsonConverter
import com.mostafatamer.chatwithme.viewModels.abstract.StompConnection

class FriendRequestViewModel(
    private val friendshipRepository: FriendshipRepository,
    private val stompService: StompService,
) : ViewModel(), StompConnection {
    val friendRequests = mutableStateListOf<FriendRequestDto>()

    init {
        ensureStompConnected()
    }

    fun observeFriendRequestsAndLoadFriends() {
        val topic =
            "${WebSocketPaths.SendFriendRequestMessageBroker.path}/${AppUser.getInstance().username}"
        stompService.topicListener(
            topic, onSubscribe = {
                loadFriends()
            }
        ) {

            val friendRequestDto = JsonConverter.getInstance()
                .fromJson(it.payload, FriendRequestDto::class.java)

            if (!friendRequests.contains(friendRequestDto)) {
                friendRequests.add(friendRequestDto)
            }
        }
    }


    private fun loadFriends() {
        friendshipRepository.getFriendRequests()
            .setOnSuccess { apiResponse ->
                println(apiResponse)
                apiResponse.data?.let { friendRequests ->
                    this.friendRequests.clear()
                    repeat(20) { this.friendRequests.addAll(friendRequests) }
                }
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
                    onAcceptFriendRequest(true)
                } else {
                    onAcceptFriendRequest(false)
                }


            }.execute()
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


    override fun ensureStompConnected() {
        ensureStompConnected(stompService)
    }

    override fun cleanUp() {
        cleanUp(stompService)
    }
}