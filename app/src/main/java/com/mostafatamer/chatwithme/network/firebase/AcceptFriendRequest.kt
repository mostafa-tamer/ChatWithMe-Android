package com.mostafatamer.chatwithme.network.firebase

import com.mostafatamer.chatwithme.network.entity.dto.User

data class AcceptFriendRequest(
    val receiver: User,
)