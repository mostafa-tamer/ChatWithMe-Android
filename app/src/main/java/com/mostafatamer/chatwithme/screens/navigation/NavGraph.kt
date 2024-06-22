package com.mostafatamer.chatwithme.screens.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.mostafatamer.chatwithme.AppDependencies
import com.mostafatamer.chatwithme.Singleton.CurrentScreen
import com.mostafatamer.chatwithme.enumeration.Screens
import com.mostafatamer.chatwithme.main_activity.StompConnectionHandler
import com.mostafatamer.chatwithme.network.entity.dto.ChatDto
import com.mostafatamer.chatwithme.network.repository.AuthenticationRepository
import com.mostafatamer.chatwithme.screens.friend_chat.FriendChatScreen
import com.mostafatamer.chatwithme.screens.friend_chat.FriendChatViewModelFactory
import com.mostafatamer.chatwithme.screens.friend_chat.view_model.FriendChatViewModel
import com.mostafatamer.chatwithme.screens.login_screen.LoginScreen
import com.mostafatamer.chatwithme.screens.login_screen.LoginViewModel
import com.mostafatamer.chatwithme.screens.main_screen.navigation.MainScreen
import com.mostafatamer.chatwithme.screens.signup_screen.SignUpScreen
import com.mostafatamer.chatwithme.screens.signup_screen.SignUpViewModel
import com.mostafatamer.chatwithme.services.StompService
import com.mostafatamer.chatwithme.utils.JsonConverter
import com.mostafatamer.chatwithme.utils.getStompClient

@Composable
fun NavGraph(navController: NavHostController, appDependencies: AppDependencies) {
    val lifecycle = LocalLifecycleOwner.current.lifecycle
    val context = LocalContext.current

    NavHost(navController = navController, startDestination = Routs.Login.route) {
        composable(Routs.SignUp.route) {
            val viewModel by remember {
                mutableStateOf(
                    SignUpViewModel(
                        AuthenticationRepository(
                            appDependencies.retrofit
                        )
                    )
                )
            }

            SignUpScreen(viewModel, navController)
        }

        composable(Routs.Login.route) {
            val viewModel = hiltViewModel<LoginViewModel>()
            var legalToLoginScreen by remember { mutableStateOf(false) }

            LaunchedEffect(key1 = Unit) {
                viewModel.validateRegisteredUser {
                    if (it) {
                        navController.navigate(Routs.Main.route) {
                            popUpTo(Routs.Login.route) {
                                inclusive = true
                            }
                        }
                    } else {
                        legalToLoginScreen = true
                    }
                }
            }

            if (legalToLoginScreen) LoginScreen(viewModel, navController)
        }

        composable(Routs.Main.route) {
            MainScreen(navController, appDependencies)
        }

        composable(Routs.FriendChatRouts.route) { navBackstackEntry ->
            val chatJson = navBackstackEntry.arguments?.getString("chat_dto")
            val chat = JsonConverter.fromJson(chatJson, ChatDto::class.java)

            val stompService by remember {
                mutableStateOf(
                    StompService(
                        getStompClient(appDependencies.userToken)
                    )
                )
            }

            val viewModelFactory: FriendChatViewModelFactory.Factory = hiltViewModel()
            val chatViewModel: FriendChatViewModel = viewModel(factory = FriendChatViewModelFactory.provideFactory(viewModelFactory, chatDto))

            val viewModel = hiltViewModel<FriendChatViewModel>()


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


