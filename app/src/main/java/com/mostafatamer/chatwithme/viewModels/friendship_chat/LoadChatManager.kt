package com.mostafatamer.chatwithme.viewModels.friendship_chat

import com.mostafatamer.chatwithme.network.entity.dto.ChatDto
import com.mostafatamer.chatwithme.viewModels.friendship_chat.FriendshipChatViewModel.ChatWithMissingMessages

class LoadChatManager(private val friendChatViewModel: FriendshipChatViewModel) {

    private val loadedChats: MutableSet<ChatDto> = mutableSetOf()

    fun addChat(chatDto: ChatDto) {
        if (!loadedChats.contains(chatDto)) {
            val chatWithMissingMessages = ChatWithMissingMessages(chatDto, 0)

            friendChatViewModel.chats.add(chatWithMissingMessages)
            loadedChats.add(chatDto)
        }
    }

}