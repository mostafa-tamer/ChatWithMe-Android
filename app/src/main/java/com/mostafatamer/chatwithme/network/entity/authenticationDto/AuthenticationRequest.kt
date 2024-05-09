package com.mostafatamer.chatwithme.network.entity.authenticationDto


data class AuthenticationRequest(
    val username: String,
    val password: String,
    val firebaseToken: String,
)