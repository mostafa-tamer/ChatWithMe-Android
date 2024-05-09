package com.mostafatamer.chatwithme.network.api

import com.mostafatamer.chatwithme.network.entity.dto.FriendRequestDto
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path

interface ChatService {
    @GET("/friendRequests/{identificationKey}")
    fun getFriendRequests(@Path("identificationKey") identificationKey: String): Call<List<FriendRequestDto>>

}
