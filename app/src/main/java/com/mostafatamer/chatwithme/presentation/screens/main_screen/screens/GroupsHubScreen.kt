package com.mostafatamer.chatwithme.presentation.screens.main_screen.screens


import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.mostafatamer.chatwithme.presentation.components.OutlinedTextFieldFullWidth
import com.mostafatamer.chatwithme.presentation.components.EmptyList
import com.mostafatamer.chatwithme.presentation.screens.main_screen.components.Card
import com.mostafatamer.chatwithme.presentation.screens.main_screen.viewmodels.GroupsHubViewmodel
import com.mostafatamer.chatwithme.presentation.navigation.Routs
import com.mostafatamer.chatwithme.utils.paginationConfiguration
import com.mostafatamer.chatwithme.utils.showToast


@Composable
fun GroupsHubScreen(
    viewModel: GroupsHubViewmodel,
    navController: NavHostController,
) {

    LaunchedEffect(Unit) {
        viewModel.reset()
        viewModel.observeNewChats()
        viewModel.observeIfChatReceivedNewMessage()
    }

    Scaffold(
        topBar = {
            TopBar(viewModel)
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
        ) {
            if (viewModel.isThereNoGroupsChat) {
                EmptyList(text = "No groups yet")
            }

            Content(viewModel, navController)
        }
    }
}


@Composable
private fun Content(
    viewModel: GroupsHubViewmodel,
    navController: NavHostController,
) {
    val state = rememberLazyListState()

    LaunchedEffect(Unit) {
        val paginationHelper = viewModel.paginationHelper

        paginationConfiguration(
            paginationHelper,
            viewModel.chats,
            state
        ) {
            viewModel.loadChats()
        }
    }

    LazyColumn {
        items(viewModel.chats) { chatCard ->
            Card(chatCard, chatCard.chat.groupName ?: "") {
                navController.navigate(
                    Routs.GroupChat.withFriend(
                        chatCard.chat
                    )
                )
            }
        }

        if (viewModel.paginationHelper.isLoading) {
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
private fun TopBar(viewModel: GroupsHubViewmodel) {

    val showCreateGroupDialog = remember { mutableStateOf(false) }

    if (showCreateGroupDialog.value) {
        CreateGroupAlertDialog(showCreateGroupDialog, viewModel)
    }

    TopAppBar(
        colors = TopAppBarDefaults.topAppBarColors(MaterialTheme.colorScheme.primary),
        title = {
            Text(
                text = "Groups Hub",
                fontSize = 24.sp, color = MaterialTheme.colorScheme.onPrimary
            )
        },
        actions = {
            IconButton(onClick = {
                showCreateGroupDialog.value = true
            }) {
                Icon(
                    imageVector = Icons.Filled.Add, contentDescription = null,
                    tint = MaterialTheme.colorScheme.onPrimary
                )
            }
        }
    )
}

@Composable
fun CreateGroupAlertDialog(
    showCreateGroupDialog: MutableState<Boolean>,
    viewModel: GroupsHubViewmodel,
) {
    val context = LocalContext.current
    val groupName = remember { mutableStateOf("") }

    AlertDialog(
        title = { Text(text = "Create Group") },
        text = {
            OutlinedTextFieldFullWidth(groupName)
        },
        onDismissRequest = { groupName.value = "" },
        confirmButton = {
            Button(onClick = {
                if (groupName.value.isNotBlank()) {
                    viewModel.createGroup(groupName.value) {
                        if (it) {
                            showToast(context, "Group created successfully")
                            showCreateGroupDialog.value = false
                            viewModel.reset()
                            viewModel.loadChats()
                        } else {
                            showToast(context, "Failed to create group")
                        }
                    }
                } else {
                    showToast(context, "Please enter group name")
                }
            }) {
                Text(text = "Create")
            }
        },
        dismissButton = {
            Button(onClick = { showCreateGroupDialog.value = false }) {
                Text(text = "Cancel")
            }
        }
    )
}
