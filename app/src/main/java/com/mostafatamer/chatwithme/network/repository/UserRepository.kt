package com.mostafatamer.chatwithme.network.repository

import com.mostafatamer.chatwithme.network.entity.authenticationDto.AuthenticationRequest
import com.mostafatamer.chatwithme.network.entity.authenticationDto.RegistrationRequest
import com.mostafatamer.chatwithme.network.api.AuthenticationApiService
import com.mostafatamer.chatwithme.utils.CallDecorator
import retrofit2.Retrofit

class UserRepository(private val retrofit: Retrofit) {
    private val authenticationApiService = retrofit.create(AuthenticationApiService::class.java)

    fun signUp(user: RegistrationRequest) = CallDecorator(authenticationApiService.signUp(user))

    fun login(authenticationRequest: AuthenticationRequest) =
        CallDecorator(authenticationApiService.login(authenticationRequest))

}