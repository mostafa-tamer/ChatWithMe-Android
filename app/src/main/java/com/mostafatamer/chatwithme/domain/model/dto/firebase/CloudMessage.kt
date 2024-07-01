package com.mostafatamer.chatwithme.domain.model.dto.firebase

import com.mostafatamer.chatwithme.domain.model.dto.MessageType

data class CloudMessage<T>(
    val messageType: MessageType,
    val data: T,
)