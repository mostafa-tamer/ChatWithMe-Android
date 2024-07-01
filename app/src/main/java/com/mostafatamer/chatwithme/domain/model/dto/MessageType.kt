package com.mostafatamer.chatwithme.domain.model.dto

enum class MessageType(val value: String) {
    FRIEND_CHAT_MESSAGE("FRIEND_CHAT_MESSAGE"),
    FRIEND_REQUEST_ACCEPTED("FRIEND_REQUEST_ACCEPTED"),
    FRIEND_REQUEST("FRIEND_REQUEST"),
    ADD_TO_GROUP("ADD_TO_GROUP"),
}
