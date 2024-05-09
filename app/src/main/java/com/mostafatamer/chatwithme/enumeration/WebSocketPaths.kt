package com.mostafatamer.chatwithme.enumeration

sealed class WebSocketPaths(val path: String) {
    data object SendMessageRout : WebSocketPaths("/app/sendMessage")

    data object SendMessageToChatMessageBroker : WebSocketPaths("/send_message_to_chat")
    data object SendFriendRequestMessageBroker : WebSocketPaths("/send_friend_request")
    data object AcceptFriendRequestMessageBroker : WebSocketPaths("/accept_friend_request")

    fun pathVariable(path: String): String {
        return "${this.path}/$path"
    }
}