package com.mostafatamer.chatwithme.presentation.friend_chat.view_model

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.mostafatamer.chatwithme.enumeration.SharedPreferencesConstants
import com.mostafatamer.chatwithme.enumeration.WebSocketPaths
import com.mostafatamer.chatwithme.network.entity.dto.Chat
import com.mostafatamer.chatwithme.network.entity.dto.MessageDto
import com.mostafatamer.chatwithme.network.entity.dto.User
import com.mostafatamer.chatwithme.network.repository.ChatRepository
import com.mostafatamer.chatwithme.pagination.ItemPagingSource
import com.mostafatamer.chatwithme.presentation.abstract_view_models.StompViewModel
import com.mostafatamer.chatwithme.services.StompService
import com.mostafatamer.chatwithme.utils.SharedPreferencesHelper
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Named


@HiltViewModel
class ChatViewModel @Inject constructor(
    private val chatRepository: ChatRepository,
    private val stompService: StompService,
    @Named("friendship_chat_hub_shared_preferences")
    private val sharedPreferencesHelper: SharedPreferencesHelper,
    val user: User,
) : StompViewModel(stompService) {
    lateinit var chat: Chat // use init function for initialization


    private val pager = Pager(
        config = PagingConfig(pageSize = 10, enablePlaceholders = false),
        pagingSourceFactory = {
            ItemPagingSource { pageNumber, size ->
                chatRepository.chatMessages(chat.tag, pageNumber, size)
            }
        }
    )

    val items: Flow<PagingData<MessageDto>> = pager.flow
        .cachedIn(viewModelScope)


    val messages = mutableStateListOf<MessageDto>()


    fun init(chat: Chat) {
        this.chat = chat
    }

    private fun addMessage(newMessage: MessageDto) {
        val index = messages.binarySearch { newMessage.timeStamp.compareTo(it.timeStamp) }
        val insertIndex = if (index < 0) -(index + 1) else index
        messages.add(insertIndex, newMessage)
    }

    fun sendMessage(message: String) {
        val newMessage = MessageDto(
            chatTag = chat.tag,
            message = message,
            senderUsername = user.username,
            timeStamp = System.currentTimeMillis()
        )

        val topic = WebSocketPaths.SendMessageRout.path

        stompService.send(topic, newMessage)
    }

    private val loadedMessages = mutableSetOf<MessageDto>()

    fun observeAndLoadChat(onNewMessage: () -> Unit) {
        val topic = WebSocketPaths.SendMessageToChatMessageBroker.withChatTag(chat.tag)

        stompService.topicListener(
            topic, MessageDto::class.java
        ) { messageDto ->
            if (loadedMessages.contains(messageDto))
                return@topicListener

            addMessage(messageDto)
            loadedMessages.add(messageDto)
            saveLastMessageNumberOfTheChat(messageDto)
            onNewMessage.invoke()
        }
    }

    var currentPage = 0
    var hasNextPage = true
    var isLoading by mutableStateOf(false)

    fun loadChat() {

        isLoading = true
        chatRepository.chatMessages(chat.tag, currentPage, PAGE_SIZE)
            .setOnSuccess { apiResponse ->
                if (apiResponse.data != null) {
                    val messagesApi = apiResponse.data!!

                    hasNextPage = !messagesApi.last

                    messagesApi.content.forEach {
                        if (loadedMessages.contains(it)) return@forEach
                        addMessage(it)
                    }

                    val lastMessage = messages.maxByOrNull { it.messageNumber!! }

                    if (lastMessage != null) {
                        val lastMessageNumberKey = getLastMessageNumberKey()

                        sharedPreferencesHelper.setValue(
                            lastMessageNumberKey,
                            lastMessage.messageNumber!!
                        )
                    }
                }
                isLoading = false
            }.execute()
    }


    private fun getLastMessageNumberKey(): String {
        return SharedPreferencesConstants.FriendshipChatHub
            .lastMessageNumberWithChatTagAndUsername(chat.tag, user.username)
    }

    private fun saveLastMessageNumberOfTheChat(messageDto: MessageDto) {
        sharedPreferencesHelper.setValue(
            SharedPreferencesConstants.FriendshipChatHub
                .lastMessageNumberWithChatTagAndUsername(chat.tag, user.username),
            messageDto.messageNumber!!
        )
    }

    companion object {
        const val PAGE_SIZE = 25
    }
}

