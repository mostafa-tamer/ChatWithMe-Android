package com.mostafatamer.chatwithme.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.mostafatamer.chatwithme.AppDependencies
import com.mostafatamer.chatwithme.Singleton.CurrentScreen
import com.mostafatamer.chatwithme.activities.StompConnectionHandler
import com.mostafatamer.chatwithme.enumeration.Screens
import com.mostafatamer.chatwithme.enumeration.SharedPreferences
import com.mostafatamer.chatwithme.navigation.screens.LoginScreen
import com.mostafatamer.chatwithme.navigation.screens.MainScreen
import com.mostafatamer.chatwithme.network.entity.dto.ChatDto
import com.mostafatamer.chatwithme.network.repository.ChatRepository
import com.mostafatamer.chatwithme.network.repository.UserRepository
import com.mostafatamer.chatwithme.screens.FriendChatScreen
import com.mostafatamer.chatwithme.screens.SignUpScreen
import com.mostafatamer.chatwithme.services.StompService
import com.mostafatamer.chatwithme.utils.JsonConverter
import com.mostafatamer.chatwithme.utils.SharedPreferencesHelper
import com.mostafatamer.chatwithme.utils.getStompClient
import com.mostafatamer.chatwithme.viewModels.SignUpViewModel
import com.mostafatamer.chatwithme.viewModels.friend_chat.FriendChatViewModel

@Composable
fun SetupNavGraph(navController: NavHostController, appDependencies: AppDependencies) {
    val lifecycle = LocalLifecycleOwner.current.lifecycle
    val context = LocalContext.current

    NavHost(navController = navController, startDestination = ScreensRouts.Login.route) {
        composable(ScreensRouts.SignUp.route) {
            val viewModel by remember {
                mutableStateOf(
                    SignUpViewModel(
                        UserRepository(
                            appDependencies.retrofit
                        )
                    )
                )
            }

            SignUpScreen(viewModel, navController)
        }

        composable(ScreensRouts.Login.route) {
            LoginScreen(navController, appDependencies)
        }

        composable(ScreensRouts.Main.route) {
            MainScreen(navController, appDependencies)
        }

        composable(ScreensRouts.FriendChatScreensRouts.route) { navBackstackEntry ->
            val chatJson = navBackstackEntry.arguments?.getString("chat_dto")
            val chat = JsonConverter.fromJson(chatJson, ChatDto::class.java)

            val stompService by remember {
                mutableStateOf(
                    StompService(
                        getStompClient(appDependencies.userToken)
                    )
                )
            }

            val viewModel by remember {
                mutableStateOf(
                    FriendChatViewModel(
                        ChatRepository(
                            appDependencies.retrofit
                        ),
                        stompService     ,
                        SharedPreferencesHelper(
                            context,
                            SharedPreferences.FriendChat.name
                        ),
                        chat,
                        appDependencies
                    )
                )
            }



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


            StompConnectionHandler(stompService)
            FriendChatScreen(viewModel, navController, appDependencies)
        }
    }
}





