package com.mostafatamer.chatwithme.presentation.screens.main_screen.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.mostafatamer.chatwithme.R
import com.mostafatamer.chatwithme.presentation.screens.main_screen.components.Card
import com.mostafatamer.chatwithme.presentation.screens.main_screen.viewmodels.FriendshipHubViewModel
import com.mostafatamer.chatwithme.presentation.navigation.Routs
import com.mostafatamer.chatwithme.utils.paginationConfiguration


@Composable
fun FriendshipHubScreen(
    viewModel: FriendshipHubViewModel,
    navController: NavHostController,
) {
    LaunchedEffect(Unit) {
        viewModel.reset()
        viewModel.observeIfChatReceivedNewMessage()
        viewModel.observeFriendRemovedMe()
        viewModel.observeNewChats()
    }


    Scaffold(
        topBar = {
            TopBar(navController, viewModel)
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
        ) {
            if (viewModel.isThereNoChats) {
                Text(
                    text = "No Friends In List",
                    color = MaterialTheme.colorScheme.onSurface,
                    fontSize = 20.sp,
                    modifier = Modifier.align(Alignment.Center)
                )
            }

            Content(viewModel, navController)
        }
    }
}

@Composable
private fun Content(
    viewModel: FriendshipHubViewModel,
    navController: NavHostController,
) {
    val state = rememberLazyListState()

    LaunchedEffect(Unit) {
        paginationConfiguration(viewModel.paginationState, viewModel.chats, state) {
            viewModel.loadChats()
        }
    }

    LazyColumn {
        items(viewModel.chats) { chatCard ->
            val cardName =
                chatCard.chat.members.firstOrNull { it.username != viewModel.user.username }?.nickname
                    ?: "null"

            Card(chatCard, cardName) {
                navController.navigate(
                    Routs.FriendChat.withFriend(
                        chatCard.chat
                    )
                )
            }
        }

        if (viewModel.paginationState.isLoading) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = MaterialTheme.colorScheme.secondary)
                }
            }
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TopBar(
    navController: NavHostController,
    viewModel: FriendshipHubViewModel,
) {
    TopAppBar(
        colors = TopAppBarDefaults.topAppBarColors(MaterialTheme.colorScheme.primary),
        title = {
            Text(
                text = viewModel.user.nickname,
                fontSize = 24.sp, color = MaterialTheme.colorScheme.onPrimary
            )
        },
        actions = {
            IconButton(onClick = {
                viewModel.clearUserDataForAutomaticLogin()

                navController.navigate(Routs.Login.route) {
                    popUpTo(Routs.Main.route) {
                        inclusive = true
                    }
                }
            }) {
                Icon(
                    painter = painterResource(id = R.drawable.baseline_logout_24),
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onPrimary
                )
            }
        }
    )
}
