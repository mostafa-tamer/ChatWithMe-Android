package com.mostafatamer.chatwithme.domain.model.dto.firebase

import com.mostafatamer.chatwithme.domain.model.dto.dto.Chat

data class FirebaseChat(
    val title: String,
    val message: String,
    val chat: Chat
)