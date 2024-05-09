package com.mostafatamer.chatwithme.network.entity.authenticationDto

import lombok.Builder

@Builder
class RegistrationResponse {
    var username: String? = null
    var nickname: String? = null
}
