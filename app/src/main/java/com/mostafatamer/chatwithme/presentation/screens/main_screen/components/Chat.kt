package com.mostafatamer.chatwithme.presentation.screens.main_screen.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mostafatamer.chatwithme.domain.model.dto.dto.MessageDto
import com.mostafatamer.chatwithme.domain.model.dto.dto.UserDto
import com.mostafatamer.chatwithme.utils.timeMillisConverter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopBar(text: String, actions: @Composable RowScope.() -> Unit = {}) {
    TopAppBar(
        colors = TopAppBarDefaults.topAppBarColors(MaterialTheme.colorScheme.primary),
        title = {
            Text(
                text = text,
                fontSize = 24.sp,
                color = MaterialTheme.colorScheme.onPrimary
            )
        },
        actions = actions
    )
}

@Composable
fun MessageSending(
    sendMessage: (message: String) -> Unit,
) {
    var message by remember { mutableStateOf("") }

    val primaryColor = MaterialTheme.colorScheme.primary
    val onPrimaryColor = MaterialTheme.colorScheme.onPrimary

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .padding(8.dp)
            .background(Color.Transparent)
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
            colors = OutlinedTextFieldDefaults.colors(), shape = RoundedCornerShape(32.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Card(
            shape = CircleShape,
        ) {
            IconButton(
                modifier = Modifier
                    .background(Color.Gray)
                    .background(primaryColor),
                onClick = {
                    if (message.trim().isNotEmpty()) {
                        sendMessage.invoke(message)
                        message = ""
                    }
                }) {
                Icon(
                    imageVector = Icons.AutoMirrored.Default.Send,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onPrimary
                )
            }
        }
    }
}

@Composable
fun ProgressIndicator() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 16.dp),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator(color = MaterialTheme.colorScheme.secondary)
    }
}

@Composable
fun Message(messageDto: MessageDto, groupColleague: String? = null) {
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
                .padding(top = 8.dp, bottom = 8.dp),
            shape = RoundedCornerShape(8.dp)
        ) {
            Box(
                Modifier
                    .background(
                        MaterialTheme.colorScheme.secondary
                    )
                    .padding(8.dp)
            ) {
                Column(horizontalAlignment = Alignment.End) {
                    if (groupColleague != null) {
                        Text(
                            text = groupColleague, fontSize = 20.sp,
                            color = MaterialTheme.colorScheme.onSecondary,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Text(
                        text = messageDto.message, fontSize = 20.sp,
                        color = MaterialTheme.colorScheme.onSecondary
                    )
                    Text(
                        text = timeString,
                        textAlign = TextAlign.End,
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSecondary
                    )
                }
            }
        }
    }
}

@Composable
fun MessageRow(
    messageDto: MessageDto,
    appUser: UserDto,
    isGroupMessage: Boolean = false,
) {
    val isMyMessage = messageDto.sender.username == appUser.username

    Row {
        if (!isMyMessage)
            Spacer(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            )
        Message(
            messageDto,
            if (!isMyMessage && isGroupMessage) messageDto.sender.nickname else null
        )
        if (isMyMessage)
            Spacer(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            )

    }
}
