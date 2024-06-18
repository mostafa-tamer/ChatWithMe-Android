package com.mostafatamer.chatwithme.network.entity.dto

import lombok.Builder

@Builder
data class MessageDto(
    var chatTag: String,
    var message: String,
    var senderUsername: String,
    var timeStamp: Long,
    var messageNumber: Long? = null,
)