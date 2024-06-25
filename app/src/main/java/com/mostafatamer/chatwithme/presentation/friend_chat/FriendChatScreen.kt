package com.mostafatamer.chatwithme.presentation.friend_chat

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.LocalOverscrollConfiguration
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.mostafatamer.chatwithme.network.entity.dto.MessageDto
import com.mostafatamer.chatwithme.presentation.friend_chat.view_model.ChatViewModel
import com.mostafatamer.chatwithme.utils.Pagination
import com.mostafatamer.chatwithme.utils.timeMillisConverter
import kotlinx.coroutines.launch


@Composable
fun FriendChatScreen(
    viewModel: ChatViewModel,
    navController: NavHostController,
) {
    val lazyListState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()
    val onNewMessage: () -> Unit = {
        coroutineScope.launch {
            if (viewModel.messages.isNotEmpty())
                lazyListState.scrollToItem(0)
        }
    }

    LaunchedEffect(Unit) {
        viewModel.observeAndLoadChat(onNewMessage)
    }

    Box(
        modifier = Modifier
            .fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        if (viewModel.messages.isEmpty()) {
            Box(Modifier.fillMaxSize()) {
                Text(
                    text = "Chat is empty",
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.Center),
                    textAlign = TextAlign.Center
                )
            }
        }
        Scaffold(
            topBar = {
                TopBar(viewModel)
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
                    Chat(viewModel, lazyListState)
                }
                MessageSending(viewModel)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TopBar(viewModel: ChatViewModel) {
    TopAppBar(
        colors = TopAppBarDefaults.topAppBarColors(MaterialTheme.colorScheme.primary),
        title = {
            Text(
                text = viewModel.chat.members.first { it.username != viewModel.user.username }.nickname,
                fontSize = 20.sp
            )
        },
    )
}

@Composable
fun Message(messageDto: MessageDto, viewModel: ChatViewModel, isMyMessage: Boolean) {
    val timeString by remember {
        mutableStateOf(
            timeMillisConverter(
                messageDto.timeStamp
            )
        )
    }

    val screenWidth = LocalConfiguration.current.screenWidthDp

    Row(verticalAlignment = Alignment.CenterVertically) {
        Card(
            modifier = Modifier
                .sizeIn(maxWidth = (screenWidth * 0.75).dp)
                .padding(top = 8.dp, bottom = 8.dp)
        ) {
            Box(Modifier.padding(8.dp)) {
                Column(horizontalAlignment = Alignment.End) {

                    Text(text = messageDto.message, fontSize = 20.sp)
                    Text(
                        text = timeString,
                        textAlign = TextAlign.End,
                        fontSize = 12.sp
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun Chat(
    viewModel: ChatViewModel,
    state: LazyListState,
) {

    LaunchedEffect(state) {
        Pagination.pagination(
            object : Pagination {
                override fun isLoading(): Boolean = viewModel.isLoading
                override fun hasNextPage(): Boolean = viewModel.hasNextPage
                override fun totalItems(): Int = viewModel.messages.size
                override fun lazyListState(): LazyListState = state

                override fun loadPages() {
                    viewModel.loadChat()
                }

                override fun incrementPageNumber() {
                    viewModel.currentPage++
                }
            }
        )
    }

    CompositionLocalProvider(LocalOverscrollConfiguration provides null) {
        LazyColumn(
            Modifier.fillMaxSize(),
            state = state,
            reverseLayout = true,
//            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            items(viewModel.messages) { messageDto ->
                Row {
                    val isMyMessage = messageDto.senderUsername == viewModel.user.username
                    if (!isMyMessage)
                        Spacer(
                            modifier = Modifier
                                .fillMaxWidth()
                                .weight(1f)
                        )
                    Message(messageDto, viewModel, isMyMessage)
                    if (isMyMessage)
                        Spacer(
                            modifier = Modifier
                                .fillMaxWidth()
                                .weight(1f)
                        )

                }
            }

            if (viewModel.isLoading) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 16.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }
            }
        }
    }
}

@Composable
private fun MessageSending(
    viewModel: ChatViewModel,
) {
    var message by remember { mutableStateOf("") }
    val context = LocalContext.current


    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(4.dp)
    ) {
        OutlinedTextField(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            value = message,
            singleLine = true,
            onValueChange = {
                message = it
            },
            placeholder = {
                Text(text = "Enter your message")
            },
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = MaterialTheme.colorScheme.primary,
                unfocusedBorderColor = MaterialTheme.colorScheme.primary
            ), shape = RoundedCornerShape(32.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Card(
            shape = CircleShape,
        ) {
            IconButton(
                modifier = Modifier
                    .background(Color.Gray)
                    .background(MaterialTheme.colorScheme.primary),
                onClick = {
                    if (message.trim().isNotEmpty()) {
                        viewModel.sendMessage(message)
                        message = ""
                    }
                }) {
                Icon(
                    imageVector = Icons.Filled.Send,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onPrimary
                )
            }
        }
    }
}

