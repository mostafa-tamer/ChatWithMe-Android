package com.mostafatamer.chatwithme.domain.usecase.chat_usecase

import androidx.compose.runtime.mutableStateListOf
import com.mostafatamer.chatwithme.domain.model.ui.ChatCard
import com.mostafatamer.chatwithme.data.repository.realtime.StompRepository
import com.mostafatamer.chatwithme.sealed.SharedPreferencesConstants
import com.mostafatamer.chatwithme.domain.model.dto.dto.Chat
import com.mostafatamer.chatwithme.domain.model.dto.dto.UserDto
import com.mostafatamer.chatwithme.data.repository.restful.ChatRepository
import com.mostafatamer.chatwithme.utils.PaginationState
import com.mostafatamer.chatwithme.utils.SharedPreferencesHelper
import com.mostafatamer.chatwithme.utils.addSorted

abstract class AbstractHubUseCase(
    private val chatRepository: ChatRepository,
    private val chatSharedPreferences: SharedPreferencesHelper,
    protected val stompRepository: StompRepository,
    val user: UserDto,
) {

    val stompService get() = stompRepository.service

    protected val chatSet = mutableSetOf<Chat>()

    val chats = mutableStateListOf<ChatCard>()

    val paginationState = PaginationState(pageSize = 25)

    fun observeIfChatReceivedNewMessage() {
        stompRepository.observeIfChatReceivedNewMessage { messageDto ->

            val chatCard = chats.find { it.chat.tag == messageDto.chatTag }

            chatCard?.let {
                if ((chatCard.chat.lastMessage?.messageNumber
                        ?: Long.MIN_VALUE) < messageDto.messageNumber!!
                ) {
                    chats.remove(chatCard)

                    val newChat = chatCard.chat.copy(lastMessage = messageDto)
                    val newChatCard =
                        chatCard.copy(chat = newChat, missingMessages = getMissingMessages(newChat))

                    chats.addSorted(newChatCard, comparator(newChatCard.chat))
                }
            }
        }
    }

    fun reset() {
        chats.clear()
        chatSet.clear()
        paginationState.currentPage = 0
        paginationState.isLoading = false
        paginationState.hasMorePages = true
    }

    protected fun addChatToContainers(chat: Chat) {
        val missingMessages = getMissingMessages(chat)
        chats.addSorted(ChatCard(chat, missingMessages), comparator(chat))
        chatSet.add(chat)
    }

    protected fun comparator(chat: Chat): (chat: ChatCard) -> Int = { chatCard ->
        if (chatCard.chat.lastMessage != null && chat.lastMessage != null) {
            chat.lastMessage!!.timeStamp.compareTo(
                chatCard.chat.lastMessage!!.timeStamp
            )
        } else {
            if (chatCard.chat.lastMessage == null && chat.lastMessage != null) 1 else -1
        }
    }

    protected fun getMissingMessages(chat: Chat): Long? {
        val lastMessageNumber = chat.lastMessage?.messageNumber
        val lastReadMessageNumber = getLastReadMessageNumber(chat.tag)

        if (lastMessageNumber == null) {
            resetLastMessageNumber(chat.tag)
            return null
        }

        return if (lastReadMessageNumber == null) lastMessageNumber
        else lastMessageNumber - lastReadMessageNumber
    }

    private fun getLastReadMessageNumber(chatTag: String): Long? {
        return chatSharedPreferences.getLong(
            SharedPreferencesConstants.FriendshipChatHub.lastMessageNumberWithChatTagAndUsername(
                chatTag, user.username
            )
        )
    }

    private fun resetLastMessageNumber(chatTag: String) {
        chatSharedPreferences.setValue(
            SharedPreferencesConstants.FriendshipChatHub.lastMessageNumberWithChatTagAndUsername(
                chatTag, user.username
            ), null
        )
    }
}