package com.mostafatamer.chatwithme.network.api

import com.mostafatamer.chatwithme.network.entity.ApiResponse
import com.mostafatamer.chatwithme.network.entity.dto.FriendRequestDto
import com.mostafatamer.chatwithme.network.entity.dto.SendFriendRequestDto
import com.mostafatamer.chatwithme.network.entity.dto.UserDto
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.PUT
import retrofit2.http.Query

interface FriendshipApiService {
    @GET("/friendship/friends")
    fun allFriends(): Call<ApiResponse<List<UserDto>>>

    @GET("/friendship/friendRequests")
    fun getFriendRequests(): Call<ApiResponse<List<FriendRequestDto>>>

    @PUT("/friendship/acceptFriendRequest")
    fun acceptFriendRequest(@Query("senderUsername") senderUsername: String): Call<ApiResponse<UserDto>>

    @PUT("/friendship/sendFriendRequest")
    fun sendFriendRequest(@Body sendFriendRequestDto: SendFriendRequestDto): Call<ApiResponse<SendFriendRequestDto>>

    @GET("/friendship/totalNumberOfFriendRequests")
    fun numberOfFriendRequests():  Call<ApiResponse<Int>>
}
