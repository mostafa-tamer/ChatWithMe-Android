package com.mostafatamer.chatwithme.data.repository.restful

import com.mostafatamer.chatwithme.data.remote.api.FriendshipApiService
import com.mostafatamer.chatwithme.domain.model.dto.dto.SendFriendRequestDto
import com.mostafatamer.chatwithme.utils.CallDecorator
import retrofit2.Retrofit


class FriendshipRepository(retrofit: Retrofit) {
    private val apiService = retrofit.create(FriendshipApiService::class.java)

    fun getFriendRequests() =
        CallDecorator(apiService.getFriendRequests())

    fun acceptFriendRequest(senderUsername: String) =
        CallDecorator(apiService.acceptFriendRequest(senderUsername))

    fun sendFriendRequest(sendFriendRequestDto: SendFriendRequestDto) =
        CallDecorator(apiService.sendFriendRequest(sendFriendRequestDto))

    fun numberOfFriendRequests() =
        CallDecorator(apiService.numberOfFriendRequests())

    fun removeFriend(username: String) =
        CallDecorator(apiService.removeFriend(username))

    fun removeFriendRequest(username: String) =
        CallDecorator(apiService.removeFriendRequest(username))

}