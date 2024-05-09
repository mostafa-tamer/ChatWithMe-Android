package com.mostafatamer.chatwithme.network.firebase

import com.mostafatamer.chatwithme.network.entity.dto.ChatDto

data class Chat(
    val title: String,
    val message: String,
    val chatDto: ChatDto
)