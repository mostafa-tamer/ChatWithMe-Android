package com.mostafatamer.chatwithme.presentation.screens

import android.widget.Toast
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.LocalOverscrollConfiguration
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.IconButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.mostafatamer.chatwithme.R
import com.mostafatamer.chatwithme.presentation.components.EmptyList
import com.mostafatamer.chatwithme.presentation.screens.main_screen.components.MessageRow
import com.mostafatamer.chatwithme.presentation.screens.main_screen.components.MessageSending
import com.mostafatamer.chatwithme.presentation.screens.main_screen.components.ProgressIndicator
import com.mostafatamer.chatwithme.presentation.screens.main_screen.components.TopBar
import com.mostafatamer.chatwithme.presentation.viewmodels.GroupChatViewModel
import com.mostafatamer.chatwithme.utils.paginationConfiguration
import com.mostafatamer.chatwithme.utils.showToast

@Composable
fun GroupChatScreen(
    viewModel: GroupChatViewModel,
    navController: NavHostController,
) {
    val state = rememberLazyListState()

    LaunchedEffect(Unit) {
        viewModel.reset()

        viewModel.observeMessages { message ->
            if (message.sender.username == viewModel.user.username) {
                if (viewModel.messages.isNotEmpty()) {
                    state.scrollToItem(0)
                }
            }
        }
    }

    val showAddMemberAlertDialog = remember { mutableStateOf(false) }

    AddMember(showAddMemberAlertDialog, viewModel)

    val showLeaveGroupAlertDialog = remember { mutableStateOf(false) }

    LeaveGroup(showLeaveGroupAlertDialog, viewModel, navController)

    Box(
        modifier = Modifier
            .fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Scaffold(
            topBar = {
                val leavePainter = painterResource(id = R.drawable.baseline_logout_24)
                TopBar(viewModel.chat.groupName ?: "null") {
                    IconButton(onClick = { showLeaveGroupAlertDialog.value = true }) {
                        Icon(
                            painter = leavePainter,
                            contentDescription = null
                        )
                    }
                    IconButton(onClick = { showAddMemberAlertDialog.value = true }) {
                        Icon(imageVector = Icons.Filled.Add, contentDescription = null)
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

@Composable
private fun LeaveGroup(
    showLeaveGroupAlertDialog: MutableState<Boolean>,
    viewModel: GroupChatViewModel,
    navController: NavHostController,
) {
    val context = LocalContext.current
    if (showLeaveGroupAlertDialog.value) {
        AlertDialog(
            title = {
                Text(text = "Leave group")
            },
            text = {
                Text(text = "Are you sure you want to leave this group?")
            },
            onDismissRequest = { },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.leaveGroup {
                            if (it) {
                                showToast(context, "Group left successfully")
                                navController.navigateUp()
                                showLeaveGroupAlertDialog.value = false
                            } else {
                                showToast(context, "Error when leaving group")
                            }
                        }
                    }
                ) {
                    Text("Leave")
                }
            },
            dismissButton = {
                Button(
                    onClick = { showLeaveGroupAlertDialog.value = false }
                ) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
private fun AddMember(
    showAlertDialog: MutableState<Boolean>,
    viewModel: GroupChatViewModel,
) {
    var username by remember { mutableStateOf("") }

    val context = LocalContext.current

    if (showAlertDialog.value) {
        AlertDialog(
            title = {
                Text(text = "Add friend to group")
            },
            text = {
                OutlinedTextField(
                    value = username,
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text(text = "Username") },
                    onValueChange = {
                        username = it
                    }
                )
            },
            onDismissRequest = {},
            confirmButton = {
                Button(
                    onClick = {
                        if (username.isNotEmpty()) {
                            viewModel.addMember(username) {
                                if (it) {
                                    Toast.makeText(
                                        context,
                                        "User added successfully",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                    username = ""
                                    showAlertDialog.value = false
                                } else {
                                    Toast.makeText(
                                        context,
                                        "Error when adding member",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            }
                        } else {
                            Toast.makeText(
                                context,
                                "Username field is empty",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                ) {
                    Text("Send")
                }
            },
            dismissButton = {
                Button(
                    onClick = {
                        showAlertDialog.value = false
                        username = ""
                    }
                ) {
                    Text("Cancel")
                }
            }
        )
    }
}


@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun Chat(
    viewModel: GroupChatViewModel,
    state: LazyListState,
) {

    LaunchedEffect(state) {
        paginationConfiguration(viewModel.paginationState, viewModel.messages, state) {
            viewModel.loadMessages()
        }
    }

    CompositionLocalProvider(LocalOverscrollConfiguration provides null) {
        LazyColumn(
            Modifier
                .fillMaxSize(),
            state = state,
            reverseLayout = true,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            items(viewModel.messages) { messageDto ->
                MessageRow(messageDto, viewModel.user, true)
            }

            if (viewModel.paginationState.isLoading) {
                item {
                    ProgressIndicator()
                }
            }
        }
    }
}


