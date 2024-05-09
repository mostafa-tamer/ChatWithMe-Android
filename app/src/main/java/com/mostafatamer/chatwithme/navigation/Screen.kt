package com.mostafatamer.chatwithme.navigation

import com.google.gson.Gson
import com.mostafatamer.chatwithme.network.entity.dto.ChatDto

sealed class Screen(val route: String) {
    val gson = Gson()

    data object SignUp : Screen("signUp")
    data object Login : Screen("login")
    data object Main : Screen("main")
    data object FriendRequests : Screen("friend_requests")
    data object FriendChatScreen : Screen("friend_requests/{chat_dto}") {
        fun withFriend(chat: ChatDto): String {
            val chatJson = gson.toJson(chat)
            return route.replace("{chat_dto}", chatJson)
        }
    }
}
