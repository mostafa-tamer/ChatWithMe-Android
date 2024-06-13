package com.mostafatamer.chatwithme.navigation.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.mostafatamer.chatwithme.enumeration.SharedPreferences
import com.mostafatamer.chatwithme.navigation.MainScreenRouts
import com.mostafatamer.chatwithme.navigation.helper.StompConnection
import com.mostafatamer.chatwithme.network.repository.ChatRepository
import com.mostafatamer.chatwithme.network.repository.FriendshipRepository
import com.mostafatamer.chatwithme.screens.FriendsChatScreen
import com.mostafatamer.chatwithme.screens.components.BottomNavigationBar
import com.mostafatamer.chatwithme.services.StompService
import com.mostafatamer.chatwithme.static.RetrofitSingleton
import com.mostafatamer.chatwithme.static.StompClientSingleton
import com.mostafatamer.chatwithme.utils.SharedPreferencesHelper
import com.mostafatamer.chatwithme.viewModels.ChatsViewModel


@Composable
fun MainScreen(
    rememberNavController: NavHostController,
) {
    val mainNavController = rememberNavController()

    Scaffold(
        topBar = {

        },
        bottomBar = {
            BottomNavigationBar(mainNavController)
        }
    ) {
        Box(modifier = Modifier.padding(it)) {
            NavHost(
                navController = mainNavController,
                startDestination = MainScreenRouts.FriendsChat.route
            ) {
                composable(route = MainScreenRouts.FriendsChat.route) {
                    val viewModel by getViewModel()
                    StompConnection(viewModel = viewModel)
                    FriendsChatScreen(viewModel, rememberNavController)
                }
//                composable(route = MainScreenRouts.GroupChat.route) {
//
//                }
//                composable(route = MainScreenRouts.FriendsChat.route) {
//
//                }
            }
        }
    }
}

@Composable
private fun getViewModel(): MutableState<ChatsViewModel> {
    val context = LocalContext.current

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
                SharedPreferencesHelper(
                    context,
                    SharedPreferences.FriendChat.name
                ),
                SharedPreferencesHelper(
                    context,
                    SharedPreferences.Login.name
                ),
            )
        )
    }
    return viewModel
}