package com.mostafatamer.chatwithme.network.entity.dto

data class SendFriendRequestDto(
    var receiverUsername: String,
    var message: String,
)