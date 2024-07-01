package com.mostafatamer.chatwithme.sealed

sealed class WebSocketPaths(val path: String) {
    data object SendMessageToChatMessageBroker : WebSocketPaths("/send_message_to_chat") {

        fun withChatTag(chatTag: String): String =
            "${this.path}/$chatTag"
    }

    data object SendMessageRout : WebSocketPaths("/app/sendMessage")

    data object SendFriendRequestMessageBroker : WebSocketPaths("/send_friend_request")

    data object AcceptFriendRequestMessageBroker : WebSocketPaths("/accept_friend_request")

    data object AddedToGroupMessageBroker : WebSocketPaths("/added_to_group")

    data object RemoveFriendMessageBroker : WebSocketPaths("/remove_friend")

    fun withUsername(username: String): String {
        return "${this.path}/$username"
    }
}