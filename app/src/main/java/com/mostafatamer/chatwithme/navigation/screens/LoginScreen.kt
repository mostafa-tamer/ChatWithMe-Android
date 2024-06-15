package com.mostafatamer.chatwithme.navigation.screens
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavHostController
import com.mostafatamer.chatwithme.enumeration.SharedPreferences
import com.mostafatamer.chatwithme.navigation.ScreensRouts
import com.mostafatamer.chatwithme.network.repository.UserRepository
import com.mostafatamer.chatwithme.screens.LoginScreen
import com.mostafatamer.chatwithme.services.StompService
import com.mostafatamer.chatwithme.static.RetrofitSingleton
import com.mostafatamer.chatwithme.utils.SharedPreferencesHelper
import com.mostafatamer.chatwithme.viewModels.LoginViewModel


@Composable
fun LoginScreen(
    navController: NavHostController,
    stompService: StompService,
) {
    val context = LocalContext.current

    val viewModel by remember {
        mutableStateOf(
            LoginViewModel(
                UserRepository(
                    RetrofitSingleton.getInstance()
                ),
                stompService = stompService,
                SharedPreferencesHelper(context, SharedPreferences.Login.name)
            )
        )
    }

    var legalToLoginScreen by remember { mutableStateOf(false) }

    LaunchedEffect(key1 = Unit) {
        viewModel.validateRegisteredUser {
            if (it) {
                navController.navigate(ScreensRouts.Main.route) {
                    popUpTo(ScreensRouts.Login.route) {
                        inclusive = true
                    }
                }
            } else {
                legalToLoginScreen = true
            }
        }
    }

    if (legalToLoginScreen)
        LoginScreen(viewModel, navController)
}