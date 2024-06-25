package com.mostafatamer.chatwithme.presentation.main_screen

sealed class Routs(val route: String) {
    data object FriendsChat : Routs("friend_chat")
    data object GroupChat : Routs("group_chat")
    data object FriendShip : Routs("friendship")
}