package com.mostafatamer.chatwithme.presentation.main_screen.sub_screens.friendship_hub.entity

import com.mostafatamer.chatwithme.network.entity.dto.Chat

data class ChatCard(
    val chat: Chat,
    var missingMessages: Long? = null,
)