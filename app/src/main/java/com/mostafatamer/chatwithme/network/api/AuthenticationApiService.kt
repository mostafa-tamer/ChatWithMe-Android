package com.mostafatamer.chatwithme.network.api

import com.mostafatamer.chatwithme.network.entity.ApiResponse
import com.mostafatamer.chatwithme.network.entity.authenticationDto.AuthenticationRequest
import com.mostafatamer.chatwithme.network.entity.authenticationDto.AuthenticationResponse
import com.mostafatamer.chatwithme.network.entity.authenticationDto.RegistrationRequest
import com.mostafatamer.chatwithme.network.entity.dto.User
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path



interface AuthenticationApiService {
    @POST("/auth/register")
    fun signUp(@Body registrationRequest: RegistrationRequest): Call<ApiResponse<User>>

    @POST("/auth/authenticate")
    fun login(@Body authenticationRequest: AuthenticationRequest): Call<ApiResponse<AuthenticationResponse>>

    @GET("/users/friends/{id}")
    fun getFriends(@Path("id") id: String): Call<List<User>>
}