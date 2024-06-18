package com.mostafatamer.chatwithme.network.entity.dto

data class ChatDto(
    var tag: String,
    var friend: User,
    var lastMessage: MessageDto?,
)

