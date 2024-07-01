package com.mostafatamer.chatwithme.data.remote.api

import com.mostafatamer.chatwithme.domain.model.dto.ApiResponse
import com.mostafatamer.chatwithme.domain.model.dto.authenticationDto.AuthenticationRequest
import com.mostafatamer.chatwithme.domain.model.dto.authenticationDto.AuthenticationResponse
import com.mostafatamer.chatwithme.domain.model.dto.authenticationDto.RegistrationRequest
import com.mostafatamer.chatwithme.domain.model.dto.dto.UserDto
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path



interface AuthenticationApiService {
    @POST("/auth/register")
    fun signUp(@Body registrationRequest: RegistrationRequest): Call<ApiResponse<UserDto>>

    @POST("/auth/authenticate")
    fun login(@Body authenticationRequest: AuthenticationRequest): Call<ApiResponse<AuthenticationResponse>>

    @GET("/users/friends/{id}")
    fun getFriends(@Path("id") id: String): Call<List<UserDto>>
}