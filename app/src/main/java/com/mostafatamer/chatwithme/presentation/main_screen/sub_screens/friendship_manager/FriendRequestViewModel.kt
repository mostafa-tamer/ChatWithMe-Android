package com.mostafatamer.chatwithme.presentation.main_screen.sub_screens.friendship_manager

import androidx.compose.runtime.mutableStateListOf
import com.mostafatamer.chatwithme.enumeration.WebSocketPaths
import com.mostafatamer.chatwithme.network.entity.dto.FriendRequestDto
import com.mostafatamer.chatwithme.network.entity.dto.SendFriendRequestDto
import com.mostafatamer.chatwithme.network.entity.dto.User
import com.mostafatamer.chatwithme.network.repository.FriendshipRepository
import com.mostafatamer.chatwithme.presentation.abstract_view_models.StompViewModel
import com.mostafatamer.chatwithme.services.StompService
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class FriendRequestViewModel @Inject constructor(
    private val friendshipRepository: FriendshipRepository,
    private val stompService: StompService,
    private val user: User,
) : StompViewModel(stompService) {
    val friendRequests = mutableStateListOf<FriendRequestDto>()


    fun observeFriendRequestsAndLoadFriends() {
        val topic =
            "${WebSocketPaths.SendFriendRequestMessageBroker.path}/${user.username}"
        stompService.topicListener(
            topic, FriendRequestDto::class.java, onSubscribe = {
                loadFriends()
            }
        ) { friendRequestDto ->

            if (!friendRequests.contains(friendRequestDto)) {
                friendRequests.add(friendRequestDto)
            }
        }
    }


    private fun loadFriends() {
        friendshipRepository.getFriendRequests()
            .setOnSuccess { apiResponse ->
                apiResponse.data?.let { friendRequests ->
                    this.friendRequests.clear()
                    this.friendRequests.addAll(friendRequests)
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


}