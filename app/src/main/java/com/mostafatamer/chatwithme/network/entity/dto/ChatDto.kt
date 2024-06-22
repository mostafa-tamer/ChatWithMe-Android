package com.mostafatamer.chatwithme.network.entity.dto

data class ChatDto(
    var tag: String,
    var members: List<User>,
    var lastMessage: MessageDto?,
)

