package com.mostafatamer.chatwithme.screens.friend_chat.view_model

import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import com.mostafatamer.chatwithme.enumeration.WebSocketPaths
import com.mostafatamer.chatwithme.network.entity.dto.ChatDto
import com.mostafatamer.chatwithme.network.entity.dto.MessageDto
import com.mostafatamer.chatwithme.network.entity.dto.User
import com.mostafatamer.chatwithme.network.repository.ChatRepository
import com.mostafatamer.chatwithme.services.StompService
import com.mostafatamer.chatwithme.utils.SharedPreferencesHelper
import javax.inject.Inject
import javax.inject.Named

class FriendChatViewModel @Inject constructor(
    private val chatRepository: ChatRepository,
    private val stompService: StompService,
    @Named("friendship_chat_hub_shared_preferences") val sharedPreferencesHelper: SharedPreferencesHelper,
    val chatDto: ChatDto,
    val user: User,
) : ViewModel() {
    val messages = mutableStateListOf<MessageDto>()

    private val messagesManager = MessagesManager(this)

    fun sendMessage(message: String) {
        val newMessage = MessageDto(
            chatTag = chatDto.tag,
            message = message,
            senderUsername = user.username,
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

