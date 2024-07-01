package com.mostafatamer.chatwithme.data.repository.restful

import com.mostafatamer.chatwithme.data.remote.api.AuthenticationApiService
import com.mostafatamer.chatwithme.domain.model.dto.authenticationDto.AuthenticationRequest
import com.mostafatamer.chatwithme.domain.model.dto.authenticationDto.RegistrationRequest
import com.mostafatamer.chatwithme.utils.CallDecorator
import retrofit2.Retrofit

class AuthenticationRepository (retrofit: Retrofit) {
    private val authenticationApiService = retrofit.create(AuthenticationApiService::class.java)

    fun signUp(user: RegistrationRequest) = CallDecorator(authenticationApiService.signUp(user))

    fun login(authenticationRequest: AuthenticationRequest) =
        CallDecorator(authenticationApiService.login(authenticationRequest))

}