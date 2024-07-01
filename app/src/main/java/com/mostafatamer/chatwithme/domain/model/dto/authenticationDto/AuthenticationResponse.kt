package com.mostafatamer.chatwithme.domain.model.dto.authenticationDto

import com.mostafatamer.chatwithme.domain.model.dto.dto.UserDto

data class AuthenticationResponse(
    var token: String,
    var user: UserDto
)