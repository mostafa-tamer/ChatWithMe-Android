package com.mostafatamer.chatwithme.presentation.screens.main_screen.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.mostafatamer.chatwithme.R
import com.mostafatamer.chatwithme.presentation.components.SendToUserAlertDialog
import com.mostafatamer.chatwithme.presentation.screens.main_screen.viewmodels.FriendRequestViewModel

@Composable
fun FriendshipManagerScreen(
    viewModel: FriendRequestViewModel,
    rememberNavController: NavHostController,
) {
    LaunchedEffect(Unit) {
        viewModel.reset()
        viewModel.observeFriendRequestsAndLoadFriends()
    }

    Scaffold(
        topBar = { TopBar() }
    ) {
        Box(
            modifier = Modifier
                .padding(it)
                .padding(horizontal = 16.dp)
                .padding(bottom = 16.dp)
                .fillMaxSize()
        ) {
            if (viewModel.isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center),
                    color = MaterialTheme.colorScheme.secondary
                )
            }

            if (viewModel.isThereNoFriendRequests) {
                Text(
                    text = "No friend requests yet",
                    modifier = Modifier.align(Alignment.Center)
                )
            }

            val openDialog = remember { mutableStateOf(false) }


            AlertDialog(openDialog, viewModel)
            FriendRequestList(viewModel)
            SendFriendRequestButton(openDialog)
        }
    }
}

@Composable
private fun AlertDialog(
    openDialog: MutableState<Boolean>,
    viewModel: FriendRequestViewModel,
) {
    val context = LocalContext.current
    SendToUserAlertDialog(openDialog) { username, message ->
        viewModel.sendFriendRequest(username, message) {
            if (it) {
                Toast.makeText(
                    context,
                    "Friend request sent",
                    Toast.LENGTH_SHORT
                ).show()
                openDialog.value = false
            } else {
                Toast.makeText(
                    context,
                    "Error Occurred",
                    Toast.LENGTH_SHORT
                ).show()
            }
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
private fun FriendRequestList(viewModel: FriendRequestViewModel) {

    LazyColumn(Modifier.fillMaxSize()) {
        items(viewModel.friendRequests) { friendRequestDto ->
            Row(Modifier.padding(vertical = 20.dp)) {
                Column(Modifier.weight(1f)) {
                    Text(text = friendRequestDto.sender.nickname)
                    Text(text = friendRequestDto.message)
                }
                Row {
                    val context = LocalContext.current

                    Action(true) {
                        viewModel.acceptFriendRequest(friendRequestDto.sender.username) {
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
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Action(false) {
                        viewModel.removeFriendRequest(friendRequestDto) {
                            if (!it) {
                                Toast.makeText(
                                    context,
                                    "Error removing friend request",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun Action(
    isAccept: Boolean,
    onClick: () -> Unit,
) {
    Surface(shape = CircleShape) {
        IconButton(
            onClick = {
                onClick()
            },
            modifier = Modifier.background(
                if (isAccept) MaterialTheme.colorScheme.primary else Color.Red
            )

        ) {
            if (isAccept) {
                Icon(imageVector = Icons.Default.Check, contentDescription = null)
            } else {
                Icon(
                    painter = painterResource(id = R.drawable.wrong_delete_remove_trash_minus_cancel_close_2_svgrepo_com),
                    contentDescription = null,
                )
            }
        }
    }
}

