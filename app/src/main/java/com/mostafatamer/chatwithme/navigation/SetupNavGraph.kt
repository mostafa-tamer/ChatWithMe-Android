package com.mostafatamer.chatwithme.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.mostafatamer.chatwithme.enumeration.SharedPreferences
import com.mostafatamer.chatwithme.helper.SharedPreferencesHelper
import com.mostafatamer.chatwithme.navigation.screens.ChatsScreen
import com.mostafatamer.chatwithme.navigation.screens.FriendChatScreen
import com.mostafatamer.chatwithme.network.repository.FriendshipRepository
import com.mostafatamer.chatwithme.network.repository.UserRepository
import com.mostafatamer.chatwithme.screens.FriendRequestsScreen
import com.mostafatamer.chatwithme.screens.LoginScreen
import com.mostafatamer.chatwithme.screens.SignUpScreen
import com.mostafatamer.chatwithme.services.StompService
import com.mostafatamer.chatwithme.static.RetrofitSingleton
import com.mostafatamer.chatwithme.static.StompClientSingleton
import com.mostafatamer.chatwithme.viewModels.FriendRequestViewModel
import com.mostafatamer.chatwithme.viewModels.LoginViewModel
import com.mostafatamer.chatwithme.viewModels.SignUpViewModel

@Composable
fun SetupNavGraph(rememberNavController: NavHostController) {
    val context = LocalContext.current

    NavHost(navController = rememberNavController, startDestination = Screen.Login.route) {
        composable(Screen.Login.route) {
            val viewModel by remember {
                mutableStateOf(
                    LoginViewModel(
                        UserRepository(
                            RetrofitSingleton.getRetrofitInstance()
                        )
                    )
                )
            }
            LoginScreen(viewModel, rememberNavController)
        }


        composable(Screen.SignUp.route) {
            val viewModel by remember {
                mutableStateOf(
                    SignUpViewModel(
                        UserRepository(
                            RetrofitSingleton.getRetrofitInstance()
                        )
                    )
                )
            }
            SignUpScreen(viewModel, rememberNavController)
        }

        composable(Screen.Main.route) {
            ChatsScreen(rememberNavController)
        }
        composable(Screen.FriendRequests.route) {
            val viewModel by remember {
                mutableStateOf(
                    FriendRequestViewModel(
                        FriendshipRepository(
                            RetrofitSingleton.getRetrofitInstance()
                        ), StompService(
                            StompClientSingleton.createInstance()
                        ),
                    )
                )
            }
            FriendRequestsScreen(viewModel, rememberNavController)
        }

        composable(Screen.FriendChatScreen.route) { navBackstackEntry ->
            FriendChatScreen(navBackstackEntry, rememberNavController)
        }
    }
}




