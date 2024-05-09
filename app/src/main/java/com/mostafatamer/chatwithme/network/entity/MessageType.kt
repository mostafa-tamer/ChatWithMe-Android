package com.mostafatamer.chatwithme.network.entity

enum class MessageType(val value: String) {
    FRIEND_CHAT_MESSAGE("FRIEND_CHAT_MESSAGE"),
    FRIEND_REQUEST_ACCEPTED("FRIEND_REQUEST_ACCEPTED"),
    FRIEND_REQUEST("FRIEND_REQUEST")
}
