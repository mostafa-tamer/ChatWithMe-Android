package com.mostafatamer.chatwithme.network.entity.dto

import lombok.Builder

@Builder
class GroupDto {
    var IdentificationKey: String? = null //should be placed automatically when insert
    var users: List<UserDto>? = null
}
