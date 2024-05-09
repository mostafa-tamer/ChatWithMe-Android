package com.mostafatamer.chatwithme.network.entity.dto

data class FriendRequestDto(
    var sender: UserDto,
    var message: String
)