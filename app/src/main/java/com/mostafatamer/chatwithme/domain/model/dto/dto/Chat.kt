package com.mostafatamer.chatwithme.domain.model.dto.dto

data class Chat(
    var tag: String,
    var members: List<UserDto>,
    var lastMessage: MessageDto?,
    var groupName: String? = null,
)

