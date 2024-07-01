package com.mostafatamer.chatwithme.domain.model.dto.dto

data class SendFriendRequestDto(
    var receiverUsername: String,
    var message: String,
)