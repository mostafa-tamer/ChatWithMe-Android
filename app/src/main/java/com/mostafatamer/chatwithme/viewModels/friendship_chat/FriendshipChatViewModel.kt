package com.mostafatamer.chatwithme.viewModels.friendship_chat

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.mostafatamer.chatwithme.AppDependencies
import com.mostafatamer.chatwithme.enumeration.SharedPreferences
import com.mostafatamer.chatwithme.enumeration.WebSocketPaths
import com.mostafatamer.chatwithme.network.entity.dto.ChatDto
import com.mostafatamer.chatwithme.network.entity.dto.MessageDto
import com.mostafatamer.chatwithme.network.firebase.FriendRequest
import com.mostafatamer.chatwithme.network.repository.ChatRepository
import com.mostafatamer.chatwithme.network.repository.FriendshipRepository
import com.mostafatamer.chatwithme.services.StompService
import com.mostafatamer.chatwithme.utils.SharedPreferencesHelper

class FriendshipChatViewModel(
    private val chatRepository: ChatRepository,
    private val friendshipRepository: FriendshipRepository,
    private val stompService: StompService,
    val chatSharedPreferences: SharedPreferencesHelper,
    private val loginSharedPreferences: SharedPreferencesHelper,
    private val appDependencies: AppDependencies
) : ViewModel() {

    val chats = mutableStateListOf<ChatCard>()

    private val loadChatManager = LoadChatManager(this,appDependencies)
    private val friendRequests = FriendRequests(this)

    var numberOfFriendRequests by mutableIntStateOf(0)

    fun observeChatLastMessage() {
        val topic = WebSocketPaths.SendMessageToChatMessageBroker
            .withUsername(appDependencies.user.username)

        stompService.topicListener(
            topic, MessageDto::class.java
        ) { messageDto ->
            loadChatManager.onMessageReceived(messageDto)
        }
    }

    private fun loadChats() {
        chatRepository.allFriendshipChat()
            .setOnSuccess { apiResponse ->
                apiResponse.data?.let { chatResponse ->
                    loadChatManager.addChats(chatResponse)
                }
            }.execute()
    }

    fun observeNewChatAndLoadChats() {
        val topic = WebSocketPaths.AcceptFriendRequestMessageBroker
            .withUsername(appDependencies.user.username)

        stompService.topicListener(
            topic, ChatDto::class.java,
            onSubscribe = {
                loadChats()
            }
        ) { chatDto ->
            loadChatManager.addChat(chatDto)
        }
    }

    fun observeFriendRequests() {
        val topic =
            WebSocketPaths.SendFriendRequestMessageBroker
                .withUsername(appDependencies.user.username)

        stompService.topicListener(
            topic, FriendRequest::class.java, onSubscribe = {
                loadNumberOfFriendRequests()
            }
        ) { friendRequest ->
            friendRequests.newFriendRequest(friendRequest)
        }
    }

    private fun loadNumberOfFriendRequests() {
        friendshipRepository.numberOfFriendRequests()
            .setOnSuccess { apiResponse ->
                apiResponse.data?.let { numberOfFriendRequests ->
                    this.numberOfFriendRequests += numberOfFriendRequests
                }
            }.execute()
    }

    fun clearUserDataForAutomaticLogin() {
        loginSharedPreferences.setValue(SharedPreferences.Login.USER_TOKEN, null)
        loginSharedPreferences.setValue(SharedPreferences.Login.USER, null)
        loginSharedPreferences.setValue(SharedPreferences.Login.USER_TOKEN_TIME, null)
    }

    data class ChatCard(
        val chat: ChatDto,
        var missingMessages: Long? = null,
    )
}