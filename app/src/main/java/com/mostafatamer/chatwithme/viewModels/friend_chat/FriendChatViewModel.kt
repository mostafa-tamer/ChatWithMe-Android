package com.mostafatamer.chatwithme.viewModels.friend_chat

import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import com.mostafatamer.chatwithme.AppDependencies
import com.mostafatamer.chatwithme.enumeration.WebSocketPaths
import com.mostafatamer.chatwithme.network.entity.dto.ChatDto
import com.mostafatamer.chatwithme.network.entity.dto.MessageDto
import com.mostafatamer.chatwithme.network.repository.ChatRepository
import com.mostafatamer.chatwithme.services.StompService
import com.mostafatamer.chatwithme.utils.SharedPreferencesHelper

class FriendChatViewModel(
    private val chatRepository: ChatRepository,
    private val stompService: StompService,
    val sharedPreferencesHelper: SharedPreferencesHelper,
    val chatDto: ChatDto,
    private val appDependencies: AppDependencies,
) : ViewModel() {
    val messages = mutableStateListOf<MessageDto>()

    private val messagesManager = MessagesManager(this, appDependencies)

    fun sendMessage(message: String) {
        val newMessage = MessageDto(
            chatTag = chatDto.tag,
            message = message,
            senderUsername = appDependencies.user.username,
            timeStamp = System.currentTimeMillis()
        )

        val topic = WebSocketPaths.SendMessageRout.path

        stompService.send(topic, newMessage)
    }

    fun observeAndLoadChat(onNewMessage: () -> Unit) {
        val topic = WebSocketPaths.SendMessageToChatMessageBroker.withChatTag(chatDto.tag)

        stompService.topicListener(
            topic, MessageDto::class.java, onSubscribe = { loadChat(onNewMessage) }
        ) { messageDto ->
            println("messageDto")
            messagesManager.addMessage(messageDto, onNewMessage)
        }
    }


    private fun loadChat(onNewMessage: () -> Unit) {
        chatRepository.loadFriendChat(chatDto.tag)
            .setOnSuccess { apiResponse ->
                if (apiResponse.data != null) {
                    val messagesApi = apiResponse.data!!
                    messagesManager.addMessages(messagesApi, onNewMessage)
                }
            }.execute()
    }
}

