package com.mostafatamer.chatwithme.navigation

import com.mostafatamer.chatwithme.network.entity.dto.ChatDto
import com.mostafatamer.chatwithme.utils.JsonConverter

sealed class ScreensRouts(val route: String) {
    data object SignUp : ScreensRouts("signUp")
    data object Login : ScreensRouts("login")
    data object Main : ScreensRouts("main")
    data object FriendRequests : ScreensRouts("friend_requests")
    data object FriendChatScreensRouts : ScreensRouts("friend_requests/{chat_dto}") {
        fun withFriend(chat: ChatDto): String {
            val chatJson = JsonConverter.toJson(chat)
            return route.replace("{chat_dto}", chatJson)
        }
    }
}
