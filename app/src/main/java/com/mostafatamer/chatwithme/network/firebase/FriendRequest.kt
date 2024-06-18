package com.mostafatamer.chatwithme.network.firebase

import com.mostafatamer.chatwithme.network.entity.dto.User

data class FriendRequest(
    val sender: User,
    val message: String,
)

