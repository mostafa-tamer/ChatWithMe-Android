package com.mostafatamer.chatwithme.data.remote.api

import com.mostafatamer.chatwithme.domain.model.dto.dto.FriendRequestDto
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path

interface ChatService {
    @GET("/friendRequests/{identificationKey}")
    fun getFriendRequests(@Path("identificationKey") identificationKey: String): Call<List<FriendRequestDto>>

}
