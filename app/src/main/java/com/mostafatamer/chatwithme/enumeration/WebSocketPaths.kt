package com.mostafatamer.chatwithme.enumeration

sealed class WebSocketPaths(val path: String) {
    data object SendMessageRout : WebSocketPaths("/app/sendMessage") {
        fun withChatTag(chatTag: String): String =
            "${this.path}/$chatTag"
    }

    data object SendMessageToChatMessageBroker : WebSocketPaths("/send_message_to_chat") {
        fun withChatTag(chatTag: String): String =
            "${this.path}/$chatTag"
    }

    data object SendFriendRequestMessageBroker : WebSocketPaths("/send_friend_request") {
        fun withUsername(username: String): String {
            return "${this.path}/$username"
        }
    }

    data object AcceptFriendRequestMessageBroker : WebSocketPaths("/accept_friend_request") {
        fun withUsername(username: String): String {
            return "${this.path}/$username"
        }
    }
}