package com.mostafatamer.chatwithme.data.remote.api

import com.mostafatamer.chatwithme.domain.model.dto.ApiResponse
import com.mostafatamer.chatwithme.domain.model.dto.pagination.Page
import com.mostafatamer.chatwithme.domain.model.dto.dto.Chat
import com.mostafatamer.chatwithme.domain.model.dto.dto.MessageDto
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface ChatApiService {
    @GET("/chat/chats")
    fun friendshipChats(
        @Query("page") page: Int,
        @Query("size") size: Int,
    ): Call<ApiResponse<Page<Chat>>>

    @GET("/chat/messages")
    fun loadChat(
        @Query("chatTag") chatTag: String,
        @Query("page") page: Int,
        @Query("size") size: Int,
    ): Call<ApiResponse<Page<MessageDto>>>

    @GET("/chat/groupsChat")
    fun groupsChat(
        @Query("page") page: Int,
        @Query("size") size: Int,
    ): Call<ApiResponse<Page<Chat>>>


    @POST("/chat/add_friend_to_chat_group")
    fun addMember(
        @Query("friendUsername") userName: String,
        @Query("chatTag") tag: String,
    ): Call<ApiResponse<Chat>>

    @POST("/chat/leave_chat_group")
    fun leaveGroup(@Query("chatTag") tag: String): Call<ApiResponse<Chat>>

    @POST("/chat/create_group_chat")
    fun createGroup(@Query("groupName") groupName: String): Call<ApiResponse<Chat>>
}