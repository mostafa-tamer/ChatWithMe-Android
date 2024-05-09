package com.mostafatamer.chatwithme.network.firebase

import com.mostafatamer.chatwithme.network.entity.dto.UserDto

data class AcceptFriendRequest(
    val receiver: UserDto,
)