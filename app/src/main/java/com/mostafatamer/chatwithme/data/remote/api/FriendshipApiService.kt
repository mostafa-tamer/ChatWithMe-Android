package com.mostafatamer.chatwithme.data.remote.api

import com.mostafatamer.chatwithme.domain.model.dto.ApiResponse
import com.mostafatamer.chatwithme.domain.model.dto.dto.FriendRequestDto
import com.mostafatamer.chatwithme.domain.model.dto.dto.SendFriendRequestDto
import com.mostafatamer.chatwithme.domain.model.dto.dto.UserDto
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Query

interface FriendshipApiService {
    @GET("/friendship/friends")
    fun allFriends(): Call<ApiResponse<List<UserDto>>>

    @GET("/friendship/friendRequests")
    fun getFriendRequests(): Call<ApiResponse<List<FriendRequestDto>>>

    @POST("/friendship/acceptFriendRequest")
    fun acceptFriendRequest(@Query("senderUsername") senderUsername: String): Call<ApiResponse<UserDto>>

    @PUT("/friendship/sendFriendRequest")
    fun sendFriendRequest(@Body sendFriendRequestDto: SendFriendRequestDto): Call<ApiResponse<SendFriendRequestDto>>

    @GET("/friendship/totalNumberOfFriendRequests")
    fun numberOfFriendRequests(): Call<ApiResponse<Int>>

    @POST("/friendship/remove_friend")
    fun removeFriend(@Query("friendUsername") username: String): Call<ApiResponse<UserDto>>

    @POST("/friendship/removeFriendRequest")
    fun removeFriendRequest(@Query("senderUsername") username: String): Call<ApiResponse<UserDto>>
}
