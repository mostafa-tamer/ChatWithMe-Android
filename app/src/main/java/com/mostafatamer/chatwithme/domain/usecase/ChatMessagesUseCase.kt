package com.mostafatamer.chatwithme.domain.usecase

import androidx.compose.runtime.mutableStateListOf
import com.mostafatamer.chatwithme.sealed.SharedPreferencesConstants
import com.mostafatamer.chatwithme.sealed.WebSocketPaths
import com.mostafatamer.chatwithme.domain.model.dto.dto.Chat
import com.mostafatamer.chatwithme.domain.model.dto.dto.MessageDto
import com.mostafatamer.chatwithme.domain.model.dto.dto.UserDto
import com.mostafatamer.chatwithme.data.repository.restful.ChatRepository
import com.mostafatamer.chatwithme.data.services.StompService
import com.mostafatamer.chatwithme.utils.PaginationState
import com.mostafatamer.chatwithme.utils.SharedPreferencesHelper
import com.mostafatamer.chatwithme.utils.addSortedDesc
import javax.inject.Named


class ChatMessagesUseCase(
    private val chatRepository: ChatRepository,
    @Named("friendship_chat_hub_shared_preferences")
    private val sharedPreferencesHelper: SharedPreferencesHelper,
    val stompService: StompService,
    val user: UserDto,
) {
    lateinit var chat: Chat // use init function for initialization

    val messages = mutableStateListOf<MessageDto>()

    private val messagesSet = mutableSetOf<MessageDto>()

    val paginationState = PaginationState(pageSize = PAGE_SIZE)

    private lateinit var lastMessage: MessageDto

    val friend by lazy { chat.members.first { it.username != user.username } }

    fun sendMessage(message: String) {
        val newMessage = MessageDto(
            chatTag = chat.tag,
            message = message,
            sender = user,
            timeStamp = System.currentTimeMillis()
        )

        val topic = WebSocketPaths.SendMessageRout.path

        stompService.send(topic, newMessage)
    }

    fun observeMessages(onNewMessage: (MessageDto) -> Unit) {
        val topic = WebSocketPaths.SendMessageToChatMessageBroker.withChatTag(chat.tag)

        stompService.topicListener(
            topic, MessageDto::class.java
        ) { messageDto ->
            if (!messagesSet.contains(messageDto)) {
                addMessage(messageDto)
                saveLastMessageNumberOfTheChat(messageDto)
                onNewMessage.invoke(messageDto)
            }
        }
    }

    fun loadMessages(onMessagesLoaded: () -> Unit) {
        chatRepository.chatMessages(chat.tag, paginationState.currentPage, PAGE_SIZE)
            .setOnSuccess { apiResponse ->
                if (apiResponse.data != null) {
                    val messagesApi = apiResponse.data!!

                    paginationState.hasMorePages = !messagesApi.last

                    messagesApi.content.forEach {
                        if (!messagesSet.contains(it)) {
                            addMessage(it)
                        }
                    }

                    val maxMessageTimeStamp = messagesApi.content.maxByOrNull { it.timeStamp }

                    if (::lastMessage.isInitialized) {
                        maxMessageTimeStamp?.let { maxMessage ->
                            if (lastMessage.timeStamp < maxMessage.timeStamp) {
                                lastMessage = maxMessage
                                saveLastMessageNumberOfTheChat(maxMessage)
                            }
                        }
                    } else {
                        maxMessageTimeStamp?.let { maxMessage ->
                            saveLastMessageNumberOfTheChat(maxMessage)
                        }
                    }

                    onMessagesLoaded.invoke()
                }
            }.setLoadingObserver {
                paginationState.isLoading = it
            }.execute()
    }

    fun reset() {
        messages.clear()
        messagesSet.clear()
        paginationState.currentPage = 0
        paginationState.isLoading = false
        paginationState.hasMorePages = true
    }

    private fun saveLastMessageNumberOfTheChat(lastMessage: MessageDto) {
        sharedPreferencesHelper.setValue(
            SharedPreferencesConstants.FriendshipChatHub
                .lastMessageNumberWithChatTagAndUsername(chat.tag, user.username),
            lastMessage.messageNumber!!
        )
    }

    private fun addMessage(newMessage: MessageDto) {
        messagesSet.add(newMessage)
        messages.addSortedDesc(newMessage) { message ->
            message.timeStamp.compareTo(newMessage.timeStamp)
        }
    }

    companion object {
        const val PAGE_SIZE = 25
    }
}