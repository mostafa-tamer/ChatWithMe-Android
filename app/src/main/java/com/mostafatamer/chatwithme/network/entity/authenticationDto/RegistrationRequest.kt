package com.mostafatamer.chatwithme.network.entity.authenticationDto

data class RegistrationRequest(
    val username: String,
    val password: String,
    val nickname: String,
    val firebaseToken: String,
)