package com.mostafatamer.chatwithme.network.api

import com.mostafatamer.chatwithme.network.entity.ApiResponse
import com.mostafatamer.chatwithme.network.entity.Page
import com.mostafatamer.chatwithme.network.entity.dto.Chat
import com.mostafatamer.chatwithme.network.entity.dto.ChatLastMessageNumberDto
import com.mostafatamer.chatwithme.network.entity.dto.MessageDto
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface ChatApiService {
    @GET("/chat/chats")
    fun allChats(
        @Query("page") page: Int,
        @Query("size") size: Int,
    ): Call<ApiResponse<Page<Chat>>>

    @GET("/chat/messages")
    fun loadChat(
        @Query("chatTag") chatTag: String,
        @Query("page") page: Int,
        @Query("size")   size: Int,
    ): Call<ApiResponse<Page<MessageDto>>>

    @GET("/chat/messageNumbers")
    fun loadChatsLastMessageNumber(@Query("chatTags") chatTags: List<String>): Call<ApiResponse<List<ChatLastMessageNumberDto>>>

    @GET("/chat/chatLastMessageNumber")
    fun loadChatLastMessageNumber(@Query("chatTag") chatTag: String): Call<ApiResponse<ChatLastMessageNumberDto>>
}