package com.mostafatamer.chatwithme.screens.main_screen.sub_screens.friendship_hub

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.IconButton
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.mostafatamer.chatwithme.AppDependencies
import com.mostafatamer.chatwithme.R
import com.mostafatamer.chatwithme.screens.main_screen.sub_screens.friendship_hub.entity.ChatCard
import com.mostafatamer.chatwithme.screens.main_screen.sub_screens.friendship_hub.view_model.FriendshipHubViewModel
import com.mostafatamer.chatwithme.screens.navigation.Routs
import com.mostafatamer.chatwithme.utils.timeMillisConverter
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map

@Composable
fun FriendshipChatHub(
    viewModel: FriendshipHubViewModel,
    navController: NavHostController,
    appDependencies: AppDependencies,
) {
    LaunchedEffect(Unit) {
        viewModel.observeChatLastMessage()
        viewModel.observeFriendRequests()
        viewModel.observeNewChatAndLoadChats()
    }


    Scaffold(
        topBar = {
            TopBar(navController, viewModel, appDependencies)
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier.padding(paddingValues)
        ) {
            if (viewModel.chats.isEmpty()) {
                Text(text = "No Friends In List")
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
    val listState = rememberLazyListState()
    LaunchedEffect(0) {
        viewModel.loadChats()
    }
    LaunchedEffect(listState) {

        snapshotFlow { listState.layoutInfo }
            .map { layoutInfo ->
                val visibleItemsInfo = layoutInfo.visibleItemsInfo
                if (visibleItemsInfo.isNotEmpty()) {
                    val lastVisibleItem = visibleItemsInfo.last()
                    lastVisibleItem.index == layoutInfo.totalItemsCount - 1
                } else {
                    false
                }
            }
            .distinctUntilChanged()
            .collect { hasReachedEnd ->
                println("reached end $hasReachedEnd")
                if (hasReachedEnd) {
                    println("reached end")
                    viewModel.loadChats()
                }
            }
    }

    LazyColumn(state = listState) {
        itemsIndexed(viewModel.chats) { index, chatWithMissingMessages ->
            Card(chatWithMissingMessages, navController)
        }
        item {
            if (viewModel.isLoading.value) {
                CircularProgressIndicator(modifier = Modifier.padding(16.dp))
            }
        }
    }
}

@Composable
private fun Card(
    chatCard: ChatCard,
    navController: NavHostController,
) {
    Column(
//        verticalAlignment = Alignment.CenterVertically,
//        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier
            .fillMaxSize()
            .height(200.dp)
            .background(Color.Red)
            .clickable {
                navController.navigate(
                    Routs.FriendChatRouts.withFriend(
                        chatCard.chat
                    )
                )
            }
            .padding(vertical = 12.dp, horizontal = 16.dp)
    ) {
        Row {
            Text(
                text = chatCard.chat.members.toString(),
                fontSize = 20.sp,
                maxLines = 1,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier
                    .padding(end = 8.dp)
                    .weight(1f)
            )
            chatCard.chat.lastMessage?.let {
                Text(
                    text = timeMillisConverter(it.timeStamp),
                    fontSize = 16.sp,
                    maxLines = 1,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier
                        .padding(vertical = 4.dp)
                )
            }
        }

        Row {
            chatCard.chat.lastMessage?.let {
                Text(
                    text = it.message,
                    fontSize = 16.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier
                        .padding(end = 16.dp)
                        .weight(1f)
                )
            }

            if (chatCard.missingMessages != null && chatCard.missingMessages!! > 0) {
                Surface(shape = CircleShape) {
                    Box(
                        modifier = Modifier
                            .background(Color.Red)
                            .size(28.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = chatCard.missingMessages.toString(),
                            color = Color.White,
                            textAlign = TextAlign.Center,
                        )
                    }
                }
            } else {
                Box(modifier = Modifier.size(28.dp))
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TopBar(
    navController: NavHostController,
    viewModel: FriendshipHubViewModel,
    appDependencies: AppDependencies,
) {
    TopAppBar(
        colors = TopAppBarDefaults.topAppBarColors(MaterialTheme.colorScheme.primary),
        title = {
            Text(
                text = "appDependencies.user.nickname",
                fontSize = 20.sp
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
                    painter = painterResource(id = R.drawable.baseline_power_settings_new_24),
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onPrimary
                )
            }
        }
    )

}
