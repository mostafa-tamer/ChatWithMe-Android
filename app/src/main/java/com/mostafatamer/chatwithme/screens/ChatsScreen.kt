package com.mostafatamer.chatwithme.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.IconButton
import androidx.compose.material.Scaffold
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.mostafatamer.chatwithme.navigation.Screen
import com.mostafatamer.chatwithme.static.AppUser
import com.mostafatamer.chatwithme.viewModels.ChatsViewModel

@Composable
fun ChatsScreen(viewModel: ChatsViewModel, navController: NavHostController) {
    LaunchedEffect(Unit) {
        viewModel.loadAllChatsAndObserveChatsForNewMessageAndLoadLastMessageNumberOfEachChat()
        viewModel.observeFriendRequests()
        viewModel.observeNewChat()
    }

    Scaffold(
        topBar = {
            TopAppBar() {
                Text(
                    text = "Welcome ${AppUser.getInstance().nickname}",
                    modifier = Modifier.weight(1f)
                )

                IconButton(onClick = { navController.navigate(Screen.FriendRequests.route) }) {
                    Icon(imageVector = Icons.Filled.Person, contentDescription = null)

                    Text(
                        text = viewModel.numberOfFriendRequests.toString(),
                        color = Color.Red
                    )
                }
            }
        }, backgroundColor = MaterialTheme.colorScheme.background
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            LazyColumn {
                items(viewModel.chats) { chatWithMissingMessages ->
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxSize()
                    ) {
                        Text(
                            text = chatWithMissingMessages.chat.friend.nickname,
                            color = Color.White
                        )

                        Row(verticalAlignment = Alignment.CenterVertically) {
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

                            Surface(shape = CircleShape) {
                                IconButton(
                                    onClick = {
                                        navController.navigate(
                                            Screen.FriendChatScreen.withFriend(
                                                chatWithMissingMessages.chat
                                            )
                                        )
                                    },
                                    Modifier.background(Color.Green)
                                ) {
                                    Icon(
                                        imageVector = Icons.Filled.Email,
                                        contentDescription = null
                                    )
                                }
                            }
                        }

                    }
                    Spacer(modifier = Modifier.height(16.dp))
                }
            }
        }
    }
}
