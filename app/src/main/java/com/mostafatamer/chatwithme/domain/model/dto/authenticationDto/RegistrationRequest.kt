package com.mostafatamer.chatwithme.domain.model.dto.authenticationDto

data class RegistrationRequest(
    val username: String,
    val password: String,
    val nickname: String,
    val firebaseToken: String,
)