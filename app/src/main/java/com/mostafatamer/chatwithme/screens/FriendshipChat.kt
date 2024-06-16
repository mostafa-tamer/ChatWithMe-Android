package com.mostafatamer.chatwithme.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.IconButton
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material3.Divider
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.mostafatamer.chatwithme.R
import com.mostafatamer.chatwithme.navigation.ScreensRouts
import com.mostafatamer.chatwithme.Singleton.UserSingleton
import com.mostafatamer.chatwithme.viewModels.friendship_chat.FriendshipChatViewModel

@Composable
fun FriendshipChat(viewModel: FriendshipChatViewModel, navController: NavHostController) {
    LaunchedEffect(Unit) {
        viewModel.observeFriendRequests()
        viewModel.observeNewChatAndLoadChats()
    }

    Scaffold(
        topBar = {
            TopBar(navController, viewModel)
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .padding(paddingValues)
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
    viewModel: FriendshipChatViewModel,
    navController: NavHostController,
) {
    LazyColumn {
        itemsIndexed(viewModel.chats) { index, chatWithMissingMessages ->
            Card(chatWithMissingMessages, navController)
            if (index < viewModel.chats.lastIndex) {
                Divider(
                    color = Color.Gray,
                    thickness = 0.5.dp,
//                    modifier = Modifier.padding(horizontal = 16.dp)
                )
            }
        }
    }
}

@Composable
private fun Card(
    chatWithMissingMessages: FriendshipChatViewModel.ChatWithMissingMessages,
    navController: NavHostController,
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier
            .fillMaxSize()
            .clickable {
                navController.navigate(
                    ScreensRouts.FriendChatScreensRouts.withFriend(
                        chatWithMissingMessages.chat
                    )
                )
            }
            .padding(vertical = 20.dp)
    ) {
        Text(
            text = chatWithMissingMessages.chat.friend.nickname,
            fontSize = 24.sp,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier
                .padding(horizontal = 16.dp)
                .weight(1f)
        )

        chatWithMissingMessages.missingMessages?.let {
            Surface(shape = CircleShape) {
                Box(
                    modifier = Modifier
                        .background(Color.Red)
                        .size(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = chatWithMissingMessages.missingMessages.toString(),
                        color = Color.White,
                        textAlign = TextAlign.Center,
                    )
                }
            }
            Spacer(modifier = Modifier.width(16.dp))
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TopBar(
    navController: NavHostController,
    viewModel: FriendshipChatViewModel,
) {
    TopAppBar(
        colors = TopAppBarDefaults.topAppBarColors(MaterialTheme.colorScheme.primary),
        title = {
            Text(
                text = "Welcome ${UserSingleton.getInstance().nickname}",
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

//            Spacer(modifier = Modifier.width(4.dp))
//
//            IconButton(onClick = { navController.navigate(ScreensRouts.FriendRequests.route) }) {
//                Icon(
//                    imageVector = Icons.Filled.Person, contentDescription = null,
//                    tint = MaterialTheme.colorScheme.onPrimary
//                )
//
//                Text(
//                    text = viewModel.numberOfFriendRequests.toString(),
//                    color = Color.Red
//                )
//            }
        }
    )

}
