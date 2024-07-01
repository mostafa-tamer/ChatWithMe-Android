package com.mostafatamer.chatwithme.domain.model.ui

import com.mostafatamer.chatwithme.domain.model.dto.dto.Chat

data class ChatCard(
    val chat: Chat,
    var missingMessages: Long? = null,
)
