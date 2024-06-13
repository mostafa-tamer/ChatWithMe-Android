package com.mostafatamer.chatwithme.navigation

sealed class MainScreenRouts(val route: String) {
    data object FriendsChat : MainScreenRouts("friend_chat")
    data object GroupChat : MainScreenRouts("group_chat")
    data object FriendShip : MainScreenRouts("friendship")
}