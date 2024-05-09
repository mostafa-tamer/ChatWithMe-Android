package com.mostafatamer.chatwithme.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.navigation.NavHostController
import com.mostafatamer.chatwithme.enumeration.Screens
import com.mostafatamer.chatwithme.static.CurrentScreen
import com.mostafatamer.chatwithme.viewModels.FriendRequestViewModel

@Composable
fun FriendRequestsScreen(
    viewModel: FriendRequestViewModel,
    rememberNavController: NavHostController,
) {
    LaunchedEffect(key1 = Unit) {

        viewModel.observeFriendRequestsAndLoadFriends()
    }

    val lifecycle by rememberUpdatedState(newValue = LocalLifecycleOwner.current.lifecycle)

    DisposableEffect(key1 = Unit) {
        val observer = LifecycleEventObserver { _, event ->
            return@LifecycleEventObserver when (event) {
                Lifecycle.Event.ON_RESUME -> {
                    viewModel.ensureStompConnected()
                }

                else -> {}
            }
        }

        lifecycle.addObserver(observer)

        onDispose {
            lifecycle.removeObserver(observer)
            viewModel.cleanUp()
            CurrentScreen.screen = null
        }
    }


    Column(
        modifier = Modifier
            .padding(16.dp)
            .fillMaxSize()
    ) {
        SendFriendRequest(viewModel)
        FriendRequestList(viewModel)
    }
}

@Composable
private fun FriendRequestList(viewModel: FriendRequestViewModel) {
    val context = LocalContext.current

    LazyColumn {
        items(viewModel.friendRequests) {
            Row {
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
//                        Spacer(modifier = Modifier.width(8.dp))
//                        Surface(shape = CircleShape) {
//                            IconButton(
//                                onClick = { /*TODO*/ },
//                                modifier = Modifier.background(Color.Red)
//                            ) {
//                                Icon(imageVector = Icons.Filled.Close, contentDescription = null)
//                            }
//                        }
                }
            }
        }
    }
}

@Composable
private fun SendFriendRequest(
    viewModel: FriendRequestViewModel,
) {
    val context = LocalContext.current
    var username by remember { mutableStateOf("") }
    var message by remember { mutableStateOf("") }
    Row {
        Column(
            modifier = Modifier.weight(2f),
        ) {
            TextField(
                value = username,

                label = {
                    Text(text = "Username")
                },
                onValueChange = {
                    username = it
                }
            )
            Spacer(modifier = Modifier.height(8.dp))
            TextField(
                value = message,

                label = {
                    Text(text = "Message")
                },
                onValueChange = {
                    message = it
                }
            )
        }
        Spacer(modifier = Modifier.width(8.dp))
        Button(
            modifier = Modifier.weight(1f),
            onClick = {
                viewModel.sendFriendRequest(username, message) {
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
            }) {
            Text(text = "Send friend request", textAlign = TextAlign.Center)
        }
    }
}