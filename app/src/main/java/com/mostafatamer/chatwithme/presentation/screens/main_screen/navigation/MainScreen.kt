package com.mostafatamer.chatwithme.presentation.screens.main_screen.navigation

import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.mostafatamer.chatwithme.presentation.components.BottomNavigationBar
import com.mostafatamer.chatwithme.presentation.screens.main_screen.screens.FriendshipHubScreen
import com.mostafatamer.chatwithme.presentation.screens.main_screen.screens.FriendshipManagerScreen
import com.mostafatamer.chatwithme.presentation.screens.main_screen.screens.GroupsHubScreen
import com.mostafatamer.chatwithme.presentation.screens.main_screen.viewmodels.FriendRequestViewModel
import com.mostafatamer.chatwithme.presentation.screens.main_screen.viewmodels.FriendshipHubViewModel
import com.mostafatamer.chatwithme.presentation.screens.main_screen.viewmodels.GroupsHubViewmodel
import com.mostafatamer.chatwithme.utils.RealtimeLifeCycle


@Composable
fun MainScreen(
    navController: NavHostController,
) {
    val mainNavController = rememberNavController()

    Scaffold(
        bottomBar = {
            BottomNavigationBar(mainNavController)
        }
    ) {
        Box(modifier = Modifier.padding(it)) {
            NavHost(mainNavController, navController)
        }
    }
}

@Composable
private fun NavHost(
    mainNavController: NavHostController,
    navController: NavHostController,
) {
    NavHost(
        navController = mainNavController,
        startDestination = Routs.FriendsChat.route,
        enterTransition = {
            fadeIn(animationSpec = tween(750))
        },
        exitTransition = {
            fadeOut(animationSpec = tween(750))

        },
    ) {
        composable(route = Routs.FriendsChat.route) {
            val viewModel = hiltViewModel<FriendshipHubViewModel>()
            RealtimeLifeCycle(viewModel.stompLifecycleManager)
            FriendshipHubScreen(viewModel, navController)
        }

        composable(route = Routs.GroupChat.route) {
            val viewModel = hiltViewModel<GroupsHubViewmodel>()
            RealtimeLifeCycle(viewModel.stompLifecycleManager)
            GroupsHubScreen(viewModel = viewModel, navController = navController)
        }

        composable(route = Routs.FriendShip.route) {
            val viewModel = hiltViewModel<FriendRequestViewModel>()
            RealtimeLifeCycle(viewModel.stompLifecycleManager)
            FriendshipManagerScreen(viewModel, navController)
        }
    }
}
