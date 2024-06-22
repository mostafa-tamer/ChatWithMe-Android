package com.mostafatamer.chatwithme.enumeration

sealed class SharedPreferencesConstants(val name: String) {
    data object FriendshipChatHub : SharedPreferencesConstants("friend_chat") {
        private const val LAST_MESSAGE_NUMBER = "last_message_number"

        fun lastMessageNumberWithChatTagAndUsername(chatTag: String, username: String): String {
            return "$LAST_MESSAGE_NUMBER/$chatTag/$username"
        }
    }

    data object Login : SharedPreferencesConstants("auth") {
        const val USER = "user"
        const val USER_TOKEN = "user_token"
        const val USER_TOKEN_TIME = "user_token_time"
    }
}