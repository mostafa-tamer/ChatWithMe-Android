package com.mostafatamer.chatwithme.domain.model.dto.firebase

import com.mostafatamer.chatwithme.domain.model.dto.dto.UserDto

data class AcceptFriendRequest(
    val receiver: UserDto,
)