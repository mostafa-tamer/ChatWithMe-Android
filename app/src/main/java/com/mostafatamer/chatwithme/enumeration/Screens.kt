package com.mostafatamer.chatwithme.enumeration

sealed class Screens(val title: String) {
    //    data object FriendRequest : Screens("Friend Requests")
    data object ChatsScreen : Screens("Chats") {
        var chatTag: String? = null
    }
}