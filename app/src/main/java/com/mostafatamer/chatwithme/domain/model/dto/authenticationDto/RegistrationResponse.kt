package com.mostafatamer.chatwithme.domain.model.dto.authenticationDto

import lombok.Builder

@Builder
class RegistrationResponse {
    var username: String? = null
    var nickname: String? = null
}
