package com.mostafatamer.chatwithme.domain.model.dto.firebase

import com.mostafatamer.chatwithme.domain.model.dto.dto.UserDto

data class FriendRequest(
    val sender: UserDto,
    val message: String,
)

