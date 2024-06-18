package com.mostafatamer.chatwithme.navigation.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.mostafatamer.chatwithme.AppDependencies
import com.mostafatamer.chatwithme.activities.StompConnectionHandler
import com.mostafatamer.chatwithme.enumeration.SharedPreferences
import com.mostafatamer.chatwithme.navigation.MainScreenRouts
import com.mostafatamer.chatwithme.network.repository.ChatRepository
import com.mostafatamer.chatwithme.network.repository.FriendshipRepository
import com.mostafatamer.chatwithme.screens.FriendChatScreen
import com.mostafatamer.chatwithme.screens.FriendRequestsScreen
import com.mostafatamer.chatwithme.screens.FriendshipChatHub
import com.mostafatamer.chatwithme.screens.components.BottomNavigationBar
import com.mostafatamer.chatwithme.services.StompService
import com.mostafatamer.chatwithme.utils.SharedPreferencesHelper
import com.mostafatamer.chatwithme.utils.getStompClient
import com.mostafatamer.chatwithme.viewModels.FriendRequestViewModel
import com.mostafatamer.chatwithme.viewModels.friendship_chat.FriendshipChatHubViewModel


@Composable
fun MainScreen(
    navController: NavHostController,
    appDependencies: AppDependencies,
) {
    val mainNavController = rememberNavController()
    val context = LocalContext.current

    Scaffold(
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
                    val stompService by remember {
                        mutableStateOf(
                            StompService(
                                getStompClient(appDependencies.userToken)
                            )
                        )
                    }

                    val viewModel by remember {
                        mutableStateOf(
                            FriendshipChatHubViewModel(
                                ChatRepository(
                                    appDependencies.retrofit
                                ),
                                FriendshipRepository(
                                    appDependencies.retrofit
                                ),
                                stompService,
                                SharedPreferencesHelper(
                                    context,
                                    SharedPreferences.FriendChat.name
                                ),
                                SharedPreferencesHelper(
                                    context,
                                    SharedPreferences.Login.name
                                ),
                                appDependencies = appDependencies
                            )
                        )
                    }



                    StompConnectionHandler(stompService)

                    FriendshipChatHub(viewModel, navController, appDependencies)
                }
                composable(route = MainScreenRouts.GroupChat.route) {
                    Text("GroupChat")
                }

                composable(route = MainScreenRouts.FriendShip.route) {

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
                    FriendRequestsScreen(viewModel, navController)
                }
            }
        }
    }
}
