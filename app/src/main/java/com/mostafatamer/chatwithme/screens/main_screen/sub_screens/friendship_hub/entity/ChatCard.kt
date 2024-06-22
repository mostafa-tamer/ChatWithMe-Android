package com.mostafatamer.chatwithme.screens.main_screen.sub_screens.friendship_hub.entity

import com.mostafatamer.chatwithme.network.entity.dto.ChatDto

data class ChatCard(
    val chat: ChatDto,
    var missingMessages: Long? = null,
)