package com.mostafatamer.chatwithme.screens.main_screen.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.mostafatamer.chatwithme.AppDependencies
import com.mostafatamer.chatwithme.enumeration.SharedPreferencesConstants
import com.mostafatamer.chatwithme.main_activity.StompConnectionHandler
import com.mostafatamer.chatwithme.network.repository.ChatRepository
import com.mostafatamer.chatwithme.network.repository.FriendshipRepository
import com.mostafatamer.chatwithme.screens.components.BottomNavigationBar
import com.mostafatamer.chatwithme.screens.main_screen.Routs
import com.mostafatamer.chatwithme.screens.main_screen.sub_screens.friendship_hub.FriendshipChatHub
import com.mostafatamer.chatwithme.screens.main_screen.sub_screens.friendship_hub.view_model.FriendshipHubViewModel
import com.mostafatamer.chatwithme.screens.main_screen.sub_screens.friendship_manager.FriendRequestViewModel
import com.mostafatamer.chatwithme.screens.main_screen.sub_screens.friendship_manager.FriendshipManagerScreen
import com.mostafatamer.chatwithme.services.StompService
import com.mostafatamer.chatwithme.utils.SharedPreferencesHelper
import com.mostafatamer.chatwithme.utils.getStompClient


@Composable
fun MainScreen(
    navController: NavHostController,
    appDependencies: AppDependencies,
) {
    val mainNavController = rememberNavController()

    Scaffold(
        bottomBar = {
            BottomNavigationBar(mainNavController)
        }
    ) {
        Box(modifier = Modifier.padding(it)) {
            NavHost(mainNavController, appDependencies, navController)
        }
    }
}

@Composable
private fun NavHost(
    mainNavController: NavHostController,
    appDependencies: AppDependencies,
    navController: NavHostController,
) {
    NavHost(
        navController = mainNavController,
        startDestination = Routs.FriendsChat.route
    ) {
        composable(route = Routs.FriendsChat.route) {


            val viewModel = hiltViewModel<FriendshipHubViewModel>()

//            StompConnectionHandler(stompService)

            FriendshipChatHub(viewModel, navController, appDependencies)
        }
        composable(route = Routs.GroupChat.route) {
            Text("GroupChat")
        }

        composable(route = Routs.FriendShip.route) {

            val stompService by remember {
                mutableStateOf(
                    StompService(
                        getStompClient(appDependencies.userToken)
                    )
                )
            }

            val viewModel by remember {
                mutableStateOf(
                    FriendRequestViewModel(
                        FriendshipRepository(
                            appDependencies.retrofit
                        ),
                        stompService,
                        appDependencies
                    )
                )
            }

            StompConnectionHandler(stompService)
            FriendshipManagerScreen(viewModel, navController)
        }
    }
}
