package com.mostafatamer.chatwithme.network.repository

import com.mostafatamer.chatwithme.network.entity.dto.SendFriendRequestDto
import com.mostafatamer.chatwithme.network.api.FriendshipApiService
import com.mostafatamer.chatwithme.network.entity.ApiResponse
import com.mostafatamer.chatwithme.network.entity.dto.User
import com.mostafatamer.chatwithme.utils.CallDecorator
import retrofit2.Retrofit


class FriendshipRepository(retrofit: Retrofit) {
    private val apiService = retrofit.create(FriendshipApiService::class.java)


    fun getFriendRequests() =
        CallDecorator(apiService.getFriendRequests())

    fun allFriends() = CallDecorator(apiService.allFriends())

    fun acceptFriendRequest(senderUsername: String): CallDecorator<ApiResponse<User>> =
        CallDecorator(apiService.acceptFriendRequest(senderUsername))

    fun sendFriendRequest(sendFriendRequestDto: SendFriendRequestDto)  =
        CallDecorator(apiService.sendFriendRequest(sendFriendRequestDto))

    fun numberOfFriendRequests() =
        CallDecorator(apiService.numberOfFriendRequests())

}