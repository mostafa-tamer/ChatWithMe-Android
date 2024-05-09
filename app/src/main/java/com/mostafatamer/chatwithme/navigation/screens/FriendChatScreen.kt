package com.mostafatamer.chatwithme.navigation.screens

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavHostController
import com.mostafatamer.chatwithme.enumeration.Screens
import com.mostafatamer.chatwithme.enumeration.SharedPreferences
import com.mostafatamer.chatwithme.helper.SharedPreferencesHelper
import com.mostafatamer.chatwithme.navigation.helper.StompConnection
import com.mostafatamer.chatwithme.network.entity.dto.ChatDto
import com.mostafatamer.chatwithme.network.repository.ChatRepository
import com.mostafatamer.chatwithme.screens.FriendChatScreen
import com.mostafatamer.chatwithme.services.StompService
import com.mostafatamer.chatwithme.static.CurrentScreen
import com.mostafatamer.chatwithme.static.JsonConverter
import com.mostafatamer.chatwithme.static.RetrofitSingleton
import com.mostafatamer.chatwithme.static.StompClientSingleton
import com.mostafatamer.chatwithme.viewModels.FriendChatViewModel

@Composable
fun FriendChatScreen(
    navBackStackEntry: NavBackStackEntry,
    rememberNavController: NavHostController,
) {
    val chatJson = navBackStackEntry.arguments?.getString("chat_dto")
    val chat = JsonConverter.getInstance().fromJson(chatJson, ChatDto::class.java)
    val context = LocalContext.current

    val viewModel by getViewModel(context, chat)

    CurrentInflatedScreen(viewModel)
    StompConnection(viewModel)

    FriendChatScreen(viewModel, rememberNavController)
}

@Composable
private fun CurrentInflatedScreen(viewModel: FriendChatViewModel) {
    LaunchedEffect(viewModel) {
        CurrentScreen.screen = Screens.ChatsScreen.apply {
            chatTag = viewModel.chatDto.tag
        }
    }
    DisposableEffect(key1 = Unit) {
        onDispose {
            CurrentScreen.screen = null
        }
    }
}

@Composable
private fun getViewModel(
    context: Context,
    chat: ChatDto,
): MutableState<FriendChatViewModel> {
    val viewModel = remember {
        mutableStateOf(
            FriendChatViewModel(
                ChatRepository(
                    RetrofitSingleton.getRetrofitInstance()
                ),
                StompService(
                    StompClientSingleton.createInstance()
                ),
                SharedPreferencesHelper(context, SharedPreferences.FriendChat.name),
                chat
            )
        )
    }
    return viewModel
}