package com.mostafatamer.chatwithme.network.firebase

import com.mostafatamer.chatwithme.network.entity.dto.Chat

data class FirebaseChat(
    val title: String,
    val message: String,
    val chat: Chat
)