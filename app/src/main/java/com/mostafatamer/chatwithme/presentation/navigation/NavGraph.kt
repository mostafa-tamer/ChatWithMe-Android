package com.mostafatamer.chatwithme.presentation.navigation

import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.google.gson.Gson
import com.mostafatamer.chatwithme.Singleton.CurrentScreen
import com.mostafatamer.chatwithme.enumeration.Screens
import com.mostafatamer.chatwithme.network.entity.dto.Chat
import com.mostafatamer.chatwithme.presentation.friend_chat.FriendChatScreen
import com.mostafatamer.chatwithme.presentation.friend_chat.view_model.ChatViewModel
import com.mostafatamer.chatwithme.presentation.login_screen.LoginScreen
import com.mostafatamer.chatwithme.presentation.login_screen.LoginViewModel
import com.mostafatamer.chatwithme.presentation.main_screen.navigation.MainScreen
import com.mostafatamer.chatwithme.presentation.signup_screen.SignUpScreen
import com.mostafatamer.chatwithme.presentation.signup_screen.SignUpViewModel
import com.mostafatamer.chatwithme.utils.RealtimeLifeCycle

@Composable
fun NavGraph(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = Routs.Login.route,
        enterTransition = {
            fadeIn(animationSpec = tween(750))
        },
        exitTransition = {
            fadeOut(animationSpec = tween(750))

        },
        popEnterTransition = {
            fadeIn(animationSpec = tween(750))

        },
        popExitTransition = {
            fadeOut(animationSpec = tween(750))
        }
    ) {
        composable(Routs.SignUp.route) {
            val viewModel = hiltViewModel<SignUpViewModel>()
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
            MainScreen(navController)
        }

        composable(Routs.FriendChatRouts.route) { navBackstackEntry ->
            val chatJson = navBackstackEntry.arguments?.getString("chat_dto")
            val chat = Gson().fromJson(chatJson, Chat::class.java)

//            val userJson = sharedPreferencesHelper.getString(SharedPreferencesConstants.Authentication.USER)!!
//
//            gson.fromJson(userJson, User::class.java)

            val viewModel = hiltViewModel<ChatViewModel>()
            viewModel.init(chat)

            LaunchedEffect(viewModel) {
                CurrentScreen.screen = Screens.ChatsScreen.apply {
                    chatTag = chat.tag
                }
            }

            DisposableEffect(key1 = Unit) {
                onDispose {
                    CurrentScreen.screen = null
                }
            }

            RealtimeLifeCycle(viewModel)

            FriendChatScreen(viewModel, navController)
        }
    }
}


