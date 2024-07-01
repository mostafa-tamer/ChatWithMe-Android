package com.mostafatamer.chatwithme.domain.model.dto.dto

data class FriendRequestDto(
    var sender: UserDto,
    var message: String
)