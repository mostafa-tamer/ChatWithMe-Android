package com.mostafatamer.chatwithme.network.firebase

import com.mostafatamer.chatwithme.network.entity.dto.UserDto

data class FriendRequest(
    val sender: UserDto,
    val message: String,
)

