package com.mostafatamer.chatwithme.presentation.main_screen.sub_screens.friendship_manager

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController

@Composable
fun FriendshipManagerScreen(
    viewModel: FriendRequestViewModel,
    rememberNavController: NavHostController,
) {
    LaunchedEffect(key1 = Unit) {
        viewModel.observeFriendRequestsAndLoadFriends()
    }

    Scaffold(
        topBar = {
            TopBar()
        }
    ) {
        Box(
            modifier = Modifier
                .padding(it)
                .padding(16.dp)
                .fillMaxSize()
        ) {
            val openDialog = remember { mutableStateOf(false) }
            AlertDialog(openDialog, viewModel)

            FriendRequestList(viewModel)
            SendFriendRequestButton(openDialog)
        }
    }
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
private fun TopBar() {
    TopAppBar(
        title = {
            Text(
                text = "Friend Requests", fontSize = 20.sp,
                color = MaterialTheme.colorScheme.onPrimary
            )
        },
        colors = TopAppBarDefaults.topAppBarColors(MaterialTheme.colorScheme.primary)
    )
}

@Composable
private fun BoxScope.SendFriendRequestButton(openDialog: MutableState<Boolean>) {
    Button(
        modifier = Modifier
            .fillMaxWidth()
            .align(Alignment.BottomCenter),
        onClick = {
            openDialog.value = true
        }
    ) {
        Text(text = "Send friend request", textAlign = TextAlign.Center)
    }
}

@Composable
private fun AlertDialog(
    openDialog: MutableState<Boolean>,
    viewModel: FriendRequestViewModel,
) {
    val context = LocalContext.current

    val username = remember { mutableStateOf("") }
    val message = remember { mutableStateOf("") }

    if (openDialog.value) {
        AlertDialog(
            onDismissRequest = {},
            title = { Text(text = "Send Friend Request") },
            text = {
                SendFriendRequest(username, message)
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.sendFriendRequest(username.value, message.value) {
                            if (it) {
                                Toast.makeText(
                                    context,
                                    "Friend request sent",
                                    Toast.LENGTH_SHORT
                                ).show()
                            } else {
                                Toast.makeText(
                                    context,
                                    "Error Occurred",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                    }
                ) {
                    Text("Send")
                }
            },
            dismissButton = {
                Button(
                    onClick = { openDialog.value = false }
                ) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
private fun BoxScope.FriendRequestList(viewModel: FriendRequestViewModel) {

    if (viewModel.friendRequests.isEmpty()) {
        Text(
            text = "No friend requests yet",
            modifier = Modifier.align(Alignment.Center)
        )
    }


    val context = LocalContext.current
    LazyColumn(Modifier.fillMaxSize()) {
        items(viewModel.friendRequests) {
            Row(Modifier.padding(vertical = 20.dp)) {
                Column(Modifier.weight(1f)) {
                    Text(text = it.sender.nickname)
                    Text(text = it.message)
                }
                Row {
                    Surface(shape = CircleShape) {
                        IconButton(
                            onClick = {
                                viewModel.acceptFriendRequest(it.sender.username) {
                                    if (it) {
                                        Toast.makeText(
                                            context,
                                            "You are friends now",
                                            Toast.LENGTH_SHORT
                                        ).show()

                                    } else {
                                        Toast.makeText(
                                            context,
                                            "Error Occurred",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }
                                }
                            },
                            modifier = Modifier.background(Color.Green)
                        ) {
                            Icon(imageVector = Icons.Filled.Check, contentDescription = null)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun SendFriendRequest(
    username: MutableState<String>,
    message: MutableState<String>,
) {

    Column {
        TextField(
            value = username.value,
            modifier = Modifier.fillMaxWidth(),
            label = {
                Text(text = "Username")
            },
            onValueChange = {
                username.value = it
            }
        )
        Spacer(modifier = Modifier.height(8.dp))
        TextField(
            modifier = Modifier.fillMaxWidth(),
            value = message.value,
            minLines = 3, maxLines = 3,
            label = {
                Text(text = "Message")
            },
            onValueChange = {
                message.value = it
            }
        )
    }
}