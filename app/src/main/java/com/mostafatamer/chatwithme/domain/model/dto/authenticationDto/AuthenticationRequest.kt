package com.mostafatamer.chatwithme.domain.model.dto.authenticationDto


data class AuthenticationRequest(
    val username: String,
    val password: String,
    val firebaseToken: String,
)