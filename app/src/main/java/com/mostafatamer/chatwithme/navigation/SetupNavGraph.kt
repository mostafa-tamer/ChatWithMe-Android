package com.mostafatamer.chatwithme.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.mostafatamer.chatwithme.navigation.screens.FriendChatScreen
import com.mostafatamer.chatwithme.navigation.screens.LoginScreen
import com.mostafatamer.chatwithme.navigation.screens.MainScreen
import com.mostafatamer.chatwithme.network.repository.UserRepository
import com.mostafatamer.chatwithme.screens.SignUpScreen
import com.mostafatamer.chatwithme.services.StompService
import com.mostafatamer.chatwithme.static.RetrofitSingleton
import com.mostafatamer.chatwithme.viewModels.SignUpViewModel

@Composable
fun SetupNavGraph(navController: NavHostController, stompService: StompService) {

    NavHost(navController = navController, startDestination = ScreensRouts.Login.route) {
        composable(ScreensRouts.SignUp.route) {
            val viewModel by remember {
                mutableStateOf(
                    SignUpViewModel(
                        UserRepository(
                            RetrofitSingleton.getInstance()
                        )
                    )
                )
            }

            SignUpScreen(viewModel, navController)
        }

        composable(ScreensRouts.Login.route) {
            LoginScreen(navController, stompService)
        }

        composable(ScreensRouts.Main.route) {
            MainScreen(navController, stompService)
        }

        composable(ScreensRouts.FriendChatScreensRouts.route) { navBackstackEntry ->
            FriendChatScreen(navBackstackEntry, navController, stompService)
        }
    }
}





