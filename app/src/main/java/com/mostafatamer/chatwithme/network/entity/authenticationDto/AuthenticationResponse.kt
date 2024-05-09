package com.mostafatamer.chatwithme.network.entity.authenticationDto

import com.mostafatamer.chatwithme.network.entity.dto.UserDto

data class AuthenticationResponse(
    var token: String,
    var user: UserDto
)