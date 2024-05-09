package com.mostafatamer.chatwithme.network.repository

import com.mostafatamer.chatwithme.network.api.ChatApiService
import com.mostafatamer.chatwithme.utils.CallDecorator
import retrofit2.Retrofit


class ChatRepository(retrofit: Retrofit) {
    private val apiService = retrofit.create(ChatApiService::class.java)

    fun allFriendsChat() = CallDecorator(apiService.allChats())

    fun loadFriendChat(chatTag: String) = CallDecorator(apiService.loadFriendChat(chatTag))

    fun loadChatsLastMessageNumber(chatTags: List<String>) =
        CallDecorator(apiService.loadChatsLastMessageNumber(chatTags))

 fun loadChatLastMessageNumber(chatTag: String) =
        CallDecorator(apiService.loadChatLastMessageNumber(chatTag))
}