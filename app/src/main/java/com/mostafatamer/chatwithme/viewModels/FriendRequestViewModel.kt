package com.mostafatamer.chatwithme.viewModels

import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mostafatamer.chatwithme.enumeration.SharedPreferences
import com.mostafatamer.chatwithme.enumeration.WebSocketPaths
import com.mostafatamer.chatwithme.helper.SharedPreferencesHelper
import com.mostafatamer.chatwithme.network.entity.dto.FriendRequestDto
import com.mostafatamer.chatwithme.network.entity.dto.SendFriendRequestDto
import com.mostafatamer.chatwithme.network.repository.FriendshipRepository
import com.mostafatamer.chatwithme.static.AppUser
import com.mostafatamer.chatwithme.static.JsonConverter
import com.mostafatamer.chatwithme.services.StompService
import com.mostafatamer.chatwithme.viewModels.abstract.StompConnection
import kotlinx.coroutines.launch

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
                apiResponse.data?.let {
                    friendRequests.clear()
                    friendRequests.addAll(it)
                }
            }.execute()
    }

    fun acceptFriendRequest(
        senderUsername: String,
        onAcceptFriendRequest: (succeeded: Boolean) -> Unit,
    ) {
        friendshipRepository.acceptFriendRequest(senderUsername)
            .setOnSuccess {
                onAcceptFriendRequest(it.data != null)
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