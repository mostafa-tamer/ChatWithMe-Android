package com.mostafatamer.chatwithme.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.mostafatamer.chatwithme.enumeration.Screens
import com.mostafatamer.chatwithme.network.entity.dto.MessageDto
import com.mostafatamer.chatwithme.static.UserSingleton
import com.mostafatamer.chatwithme.static.CurrentScreen
import com.mostafatamer.chatwithme.viewModels.FriendChatViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch


@Composable
fun FriendChatScreen(viewModel: FriendChatViewModel, navController: NavHostController) {
    val lazyListState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        CurrentScreen.screen = Screens.ChatsScreen.apply {
            chatTag = viewModel.chatDto.tag
        }

        viewModel.setOnNewMessageReceived {
            scrollToLastMessage(coroutineScope, lazyListState, viewModel)
        }
        viewModel.observeAndLoadChat()
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
        Column {
            TitleBar(viewModel)

            Column(
                Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {


                Chat(lazyListState, viewModel)
                Spacer(modifier = Modifier.height(16.dp))
                MessageSending(viewModel)
            }
        }
    }
}

@Composable
private fun TitleBar(viewModel: FriendChatViewModel) {
    Box(
        contentAlignment = Alignment.CenterStart,
        modifier = Modifier
            .background(MaterialTheme.colorScheme.primary)
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Text(
            text = viewModel.chatDto.friend.nickname,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onPrimary
        )
    }
}

@Composable
fun Message(messageDto: MessageDto, viewModel: FriendChatViewModel, isMyMessage: Boolean) {
    val timeString by remember {
        mutableStateOf(
            viewModel.timeMillisConverter(
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
//                    if (!isMyMessage) {
//                        Spacer(modifier = Modifier.width(8.dp))
//                        Text(
//                            text = viewModel.messages,
//                            fontSize = 12.sp,
//                            fontWeight = FontWeight.Bold,
//                            color = MaterialTheme.colorScheme.primary
//                        )
//                    }
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

@Composable
private fun ColumnScope.Chat(
    lazyListState: LazyListState,
    viewModel: FriendChatViewModel,
) {
    LazyColumn(
        Modifier
            .fillMaxSize()
            .weight(1f),
        state = lazyListState
    ) {
        items(viewModel.messages) {
            Row {
                val isMyMessage = it.senderUsername == UserSingleton.getInstance().username
                if (!isMyMessage)
                    Spacer(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f)
                    )
                Message(it, viewModel, isMyMessage)
                if (isMyMessage)
                    Spacer(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f)
                    )
            }
        }
    }
}

@Composable
private fun MessageSending(
    viewModel: FriendChatViewModel,
) {
    var message by remember { mutableStateOf("") }
    val context = LocalContext.current
    Row(verticalAlignment = Alignment.CenterVertically) {
        TextField(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            value = message,
            singleLine = true,
            onValueChange = {
                message = it
//                viewModel.startTyping(
//                    MessageNode(
//                        System.currentTimeMillis().toString(),
//                        it,
//                        viewModel.username
//                    )
//                )
            },
            placeholder = {
                Text(text = "Enter your message")
            }
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
                    if (message.isNotEmpty()) {
                        viewModel.sendMessage(message)
                        message = ""
                    } else {
                        Toast.makeText(context, "Please enter a message", Toast.LENGTH_SHORT).show()
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

private fun scrollToLastMessage(
    coroutineScope: CoroutineScope,
    lazyListState: LazyListState,
    viewModel: FriendChatViewModel,
) {
    coroutineScope.launch {
        if (viewModel.messages.size - 1 >= 0)
            lazyListState.scrollToItem(viewModel.messages.size - 1)
    }
}