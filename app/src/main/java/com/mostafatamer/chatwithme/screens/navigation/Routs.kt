package com.mostafatamer.chatwithme.screens.navigation

import com.mostafatamer.chatwithme.network.entity.dto.ChatDto
import com.mostafatamer.chatwithme.utils.JsonConverter

sealed class Routs(val route: String) {
    data object SignUp : Routs("signUp")
    data object Login : Routs("login")
    data object Main : Routs("main")
    data object FriendRequests : Routs("friend_requests")
    data object FriendChatRouts : Routs("friend_requests/{chat_dto}") {
        fun withFriend(chat: ChatDto): String {
            val chatJson = JsonConverter.toJson(chat)
            return route.replace("{chat_dto}", chatJson)
        }
    }
}
