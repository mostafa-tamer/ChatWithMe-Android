package com.mostafatamer.chatwithme.enumeration

sealed class SharedPreferences(val name: String) {
    data object FriendChat : SharedPreferences("friend_chat") {
        private const val LAST_MESSAGE_NUMBER = "last_message_number"

        fun lastMessageNumberWithChatTagAndUsername(chatTag: String, username: String): String {
            return "$LAST_MESSAGE_NUMBER/$chatTag/$username"
        }
    }
}