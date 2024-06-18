package com.mostafatamer.chatwithme.network.entity.dto

data class FriendRequestDto(
    var sender: User,
    var message: String
)