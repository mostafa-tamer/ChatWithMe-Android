package com.mostafatamer.chatwithme.data.repository.restful

import com.mostafatamer.chatwithme.data.remote.api.ChatApiService
import com.mostafatamer.chatwithme.utils.CallDecorator
import retrofit2.Retrofit


class ChatRepository(retrofit: Retrofit) {
    private val apiService = retrofit.create(ChatApiService::class.java)

    fun friendshipChats(pageNumber: Int, size: Int) =
        CallDecorator(apiService.friendshipChats(pageNumber, size))

    fun groupsChat(pageNumber: Int, size: Int) =
        CallDecorator(apiService.groupsChat(pageNumber, size))

    fun chatMessages(chatTag: String, pageNumber: Int, size: Int) =
        CallDecorator(apiService.loadChat(chatTag, pageNumber, size))


    fun addMember(tag: String, userName: String) =
        CallDecorator(apiService.addMember(userName, tag))

    fun leaveGroup(tag: String) =
        CallDecorator(apiService.leaveGroup(tag))

    fun createGroup(groupName: String) =
        CallDecorator(apiService.createGroup (groupName))

}