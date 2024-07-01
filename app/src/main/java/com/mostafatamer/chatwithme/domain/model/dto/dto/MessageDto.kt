package com.mostafatamer.chatwithme.domain.model.dto.dto

import lombok.Builder

@Builder
data class MessageDto(
    var chatTag: String,
    var message: String,
    var sender: UserDto,
    var timeStamp: Long,
    var messageNumber: Long? = null,
)