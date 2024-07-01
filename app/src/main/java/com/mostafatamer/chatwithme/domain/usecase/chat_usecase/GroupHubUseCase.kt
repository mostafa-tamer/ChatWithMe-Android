package com.mostafatamer.chatwithme.domain.usecase.chat_usecase

import com.mostafatamer.chatwithme.data.repository.realtime.StompRepository
import com.mostafatamer.chatwithme.domain.model.dto.dto.Chat
import com.mostafatamer.chatwithme.domain.model.dto.dto.UserDto
import com.mostafatamer.chatwithme.data.repository.restful.ChatRepository
import com.mostafatamer.chatwithme.utils.SharedPreferencesHelper

class GroupHubUseCase(
    private val chatRepository: ChatRepository,
    private val chatSharedPreferences: SharedPreferencesHelper,
    stompRepository: StompRepository,
    userDto: UserDto,
) : AbstractHubUseCase(
    chatRepository,
    chatSharedPreferences,
    stompRepository,
    userDto
) {

    fun observeNewGroupChat(onNewChat: (Chat) -> Unit) {
        stompRepository.observeNewGroupChat { chat ->
            if (!chatSet.contains(chat)) {
                addChatToContainers(chat)
                onNewChat(chat)
            }
        }
    }

    fun loadChats(onLoad: () -> Unit) {
        chatRepository.groupsChat(
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
}