package com.mostafatamer.chatwithme.domain.usecase.chat_usecase

import com.mostafatamer.chatwithme.data.repository.realtime.StompRepository
import com.mostafatamer.chatwithme.domain.model.dto.dto.Chat
import com.mostafatamer.chatwithme.domain.model.dto.dto.UserDto
import com.mostafatamer.chatwithme.data.repository.restful.ChatRepository
import com.mostafatamer.chatwithme.utils.SharedPreferencesHelper

class FriendshipHubUseCase(
    private val chatRepository: ChatRepository,
    chatSharedPreferences: SharedPreferencesHelper,
    stompRepository: StompRepository,
    user: UserDto,
) : AbstractHubUseCase(
    chatRepository, chatSharedPreferences, stompRepository, user
) {

    fun observeNewFriendshipChats(onNewChat: (Chat) -> Unit) {
        stompRepository.observeNewFriendshipChat { chat ->
            if (!chatSet.contains(chat)) {
                addChatToContainers(chat)
//                val missingMessages = getMissingMessages(chat)
//                chats.addSorted(ChatCard(chat, missingMessages), comparator(chat))
//                chatSet.add(chat)
                onNewChat(chat)
            }
        }
    }

    fun loadChats(onLoad: () -> Unit) {
        chatRepository.friendshipChats(
            paginationState.currentPage,
            paginationState.pageSize
        ).setOnSuccess { apiResponse ->
            if (apiResponse.apiError == null) {
                val data = apiResponse.data!!
                val content = data.content

                content.forEach { chat ->
                    if (!chatSet.contains(chat)) {
                        addChatToContainers(chat)
                    }
                }

                paginationState.hasMorePages = !data.last
                onLoad()
            }
        }.setLoadingObserver { isLoading ->
            paginationState.isLoading = isLoading
        }.execute()
    }

    fun removeChat(chat: Chat) {
        chats.removeIf { chat.tag == it.chat.tag }
        chatSet.removeIf { chat.tag == it.tag }
    }
}