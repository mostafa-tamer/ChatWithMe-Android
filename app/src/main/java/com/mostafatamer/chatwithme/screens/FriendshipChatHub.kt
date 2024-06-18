package com.mostafatamer.chatwithme.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
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
import com.mostafatamer.chatwithme.navigation.ScreensRouts
import com.mostafatamer.chatwithme.utils.timeMillisConverter
import com.mostafatamer.chatwithme.viewModels.friendship_chat.FriendshipChatHubViewModel

@Composable
fun FriendshipChatHub(
    viewModel: FriendshipChatHubViewModel,
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
    viewModel: FriendshipChatHubViewModel,
    navController: NavHostController,
) {
    LazyColumn {
        itemsIndexed(viewModel.chats) { index, chatWithMissingMessages ->
            Card(chatWithMissingMessages, navController)
//            if (index < viewModel.chats.lastIndex) {
//                Divider(
//                    color = Color.Gray,
//                    thickness = 0.5.dp,
//                )
//            }
        }
    }
}

@Composable
private fun Card(
    chatCard: FriendshipChatHubViewModel.ChatCard,
    navController: NavHostController,
) {
    Column(
//        verticalAlignment = Alignment.CenterVertically,
//        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier
            .fillMaxSize()
            .clickable {
                navController.navigate(
                    ScreensRouts.FriendChatScreensRouts.withFriend(
                        chatCard.chat
                    )
                )
            }
            .padding(vertical = 12.dp, horizontal = 16.dp)
    ) {
        Row {
            Text(
                text = chatCard.chat.friend.nickname,
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
    viewModel: FriendshipChatHubViewModel,
    appDependencies: AppDependencies,
) {
    TopAppBar(
        colors = TopAppBarDefaults.topAppBarColors(MaterialTheme.colorScheme.primary),
        title = {
            Text(
                text = appDependencies.user.nickname,
                fontSize = 20.sp
            )
        },
        actions = {
            IconButton(onClick = {
                viewModel.clearUserDataForAutomaticLogin()

                navController.navigate(ScreensRouts.Login.route) {
                    popUpTo(ScreensRouts.Main.route) {
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
