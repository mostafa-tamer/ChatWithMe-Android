package com.mostafatamer.chatwithme.navigation.screens

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.navigation.NavHostController
import com.mostafatamer.chatwithme.network.repository.FriendshipRepository
import com.mostafatamer.chatwithme.screens.FriendRequestsScreen
import com.mostafatamer.chatwithme.services.StompService
import com.mostafatamer.chatwithme.static.RetrofitSingleton
import com.mostafatamer.chatwithme.static.StompClientSingleton
import com.mostafatamer.chatwithme.viewModels.FriendRequestViewModel

@Composable
fun FriendRequestsScreen(navController: NavHostController, stompService: StompService) {
    val viewModel by remember {
        mutableStateOf(
            FriendRequestViewModel(
                FriendshipRepository(
                    RetrofitSingleton.getInstance()
                ),
                stompService,
            )
        )
    }

    FriendRequestsScreen(viewModel, navController)
}
