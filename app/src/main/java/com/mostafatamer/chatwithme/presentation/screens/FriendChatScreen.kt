package com.mostafatamer.chatwithme.presentation.screens

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.LocalOverscrollConfiguration
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.mostafatamer.chatwithme.R
import com.mostafatamer.chatwithme.presentation.components.DefaultAlertDialog
import com.mostafatamer.chatwithme.presentation.components.EmptyList
import com.mostafatamer.chatwithme.presentation.screens.main_screen.components.MessageRow
import com.mostafatamer.chatwithme.presentation.screens.main_screen.components.MessageSending
import com.mostafatamer.chatwithme.presentation.screens.main_screen.components.ProgressIndicator
import com.mostafatamer.chatwithme.presentation.screens.main_screen.components.TopBar
import com.mostafatamer.chatwithme.presentation.viewmodels.FriendshipChatViewModel
import com.mostafatamer.chatwithme.utils.paginationConfiguration
import com.mostafatamer.chatwithme.utils.showToast


@Composable
fun FriendChatScreen(
    viewModel: FriendshipChatViewModel,
    navController: NavHostController,
) {
    val state = rememberLazyListState()
    val context = LocalContext.current
    LaunchedEffect(Unit) {
        viewModel.reset()
        viewModel.observeFriendRemovedMe {
            navController.navigateUp()
        }
        viewModel.observeMessages { message ->
            if (message.sender.username == viewModel.user.username) {
                if (viewModel.messages.isNotEmpty()) {
                    state.scrollToItem(0)
                }
            }
        }
    }

    val showRemoveFriendAlertDialog = remember { mutableStateOf(false) }

    DefaultAlertDialog(
        showAlertDialog = showRemoveFriendAlertDialog,
        title = "Remove Friend",
        message = "Are you sure you want to remove this friend"
    ) {
        viewModel.removeFriend {
            if (it) {
                showToast(context, "Friend removed successfully")
                showRemoveFriendAlertDialog.value = false
                navController.navigateUp()
            } else {
                showToast(context, "Error removing friend")
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Scaffold(
            topBar = {
                val text = viewModel.friend.nickname
                TopBar(text) {
                    IconButton(onClick = { showRemoveFriendAlertDialog.value = true }) {
                        Icon(
                            painter = painterResource(id = R.drawable.baseline_block_24),
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onPrimary
                        )
                    }
                }
            }
        ) {
            Column(
                Modifier
                    .fillMaxSize()
                    .padding(it)
            ) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .padding(horizontal = 8.dp)
                ) {

                    if (viewModel.isThereNoMessages) {
                        EmptyList("Chat is empty")
                    }
                    Chat(viewModel, state)
                }

                MessageSending { message ->
                    viewModel.sendMessage(message)
                }
            }
        }
    }
}


@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun Chat(viewModel: FriendshipChatViewModel, state: LazyListState) {

    LaunchedEffect(state) {

        paginationConfiguration(
            paginationState = viewModel.paginationState,
            items = viewModel.messages,
            state = state
        ) {
            viewModel.loadMessages()
        }
    }

    CompositionLocalProvider(LocalOverscrollConfiguration provides null) {
        LazyColumn(
            Modifier.fillMaxSize(),
            state = state,
            reverseLayout = true,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            items(viewModel.messages) { messageDto ->
                MessageRow(messageDto = messageDto, appUser = viewModel.user)
            }
            if (viewModel.paginationState.isLoading) {
                item {
                    ProgressIndicator()
                }
            }
        }
    }
}



