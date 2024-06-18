package com.mostafatamer.chatwithme.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.mostafatamer.chatwithme.AppDependencies
import com.mostafatamer.chatwithme.Singleton.RetrofitSingleton
import com.mostafatamer.chatwithme.activities.StompConnectionHandler
import com.mostafatamer.chatwithme.navigation.screens.FriendChatScreen
import com.mostafatamer.chatwithme.navigation.screens.LoginScreen
import com.mostafatamer.chatwithme.navigation.screens.MainScreen
import com.mostafatamer.chatwithme.network.repository.UserRepository
import com.mostafatamer.chatwithme.screens.SignUpScreen
import com.mostafatamer.chatwithme.services.StompService
import com.mostafatamer.chatwithme.utils.createStompClient
import com.mostafatamer.chatwithme.viewModels.SignUpViewModel

@Composable
fun SetupNavGraph(navController: NavHostController, appDependencies: AppDependencies) {
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
            LoginScreen(navController, appDependencies)
        }

        composable(ScreensRouts.Main.route) {
            MainScreen(navController, appDependencies)
        }

        composable(ScreensRouts.FriendChatScreensRouts.route) { navBackstackEntry ->
            val stompService by remember {
                mutableStateOf(
                    StompService(
                        createStompClient(appDependencies.userToken)
                    )
                )
            }
            StompConnectionHandler(stompService)
            FriendChatScreen(navBackstackEntry, navController, stompService, appDependencies)
        }
    }
}





