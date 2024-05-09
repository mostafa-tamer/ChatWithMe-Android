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
import androidx.navigation.NavHostController
import com.mostafatamer.chatwithme.enumeration.Screens
import com.mostafatamer.chatwithme.enumeration.SharedPreferences
import com.mostafatamer.chatwithme.helper.SharedPreferencesHelper
import com.mostafatamer.chatwithme.navigation.helper.StompConnection
import com.mostafatamer.chatwithme.network.repository.ChatRepository
import com.mostafatamer.chatwithme.network.repository.FriendshipRepository
import com.mostafatamer.chatwithme.screens.ChatsScreen
import com.mostafatamer.chatwithme.services.StompService
import com.mostafatamer.chatwithme.static.CurrentScreen
import com.mostafatamer.chatwithme.static.RetrofitSingleton
import com.mostafatamer.chatwithme.static.StompClientSingleton
import com.mostafatamer.chatwithme.viewModels.ChatsViewModel
import com.mostafatamer.chatwithme.viewModels.FriendChatViewModel


@Composable
fun ChatsScreen(
    rememberNavController: NavHostController,
) {
    val context = LocalContext.current
    val viewModel by getViewModel(context)

    StompConnection(viewModel = viewModel)

    ChatsScreen(viewModel, rememberNavController)
}

@Composable
private fun getViewModel(context: Context): MutableState<ChatsViewModel> {
    val viewModel = remember {
        mutableStateOf(
            ChatsViewModel(
                ChatRepository(
                    RetrofitSingleton.getRetrofitInstance()
                ),
                FriendshipRepository(
                    RetrofitSingleton.getRetrofitInstance()
                ),
                StompService(
                    StompClientSingleton.createInstance()
                ),
                SharedPreferencesHelper(context, SharedPreferences.FriendChat.name),
            )
        )
    }
    return viewModel
}