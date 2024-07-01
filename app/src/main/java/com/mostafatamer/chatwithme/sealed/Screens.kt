package com.mostafatamer.chatwithme.sealed

sealed class Screens(val title: String) {
    //    data object FriendRequest : Screens("Friend Requests")
    data object ChatsScreen : Screens("Chats") {
        var chatTag: String? = null
    }
}