package com.mostafatamer.chatwithme.network.entity.dto

data class Chat(
    var tag: String,
    var members: List<User>,
    var lastMessage: MessageDto?,
)

