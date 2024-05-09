package com.mostafatamer.chatwithme.network.firebase

import com.mostafatamer.chatwithme.network.entity.MessageType

data class CloudMessage<T>(
    val messageType: MessageType,
    val data: T,
)