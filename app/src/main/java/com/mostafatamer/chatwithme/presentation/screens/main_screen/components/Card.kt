package com.mostafatamer.chatwithme.presentation.screens.main_screen.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Color.Companion.Red
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mostafatamer.chatwithme.domain.model.ui.ChatCard
import com.mostafatamer.chatwithme.utils.timeMillisConverter


@Composable
fun Card(
    chatCard: ChatCard,
    chatName: String,
    lastMessage: String,
    onClick: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                onClick()
            }
            .padding(vertical = 12.dp, horizontal = 16.dp)
    ) {
        Row {
            Text(
                text = chatName,
                fontSize = 20.sp,
                maxLines = 1,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier
                    .padding(end = 8.dp)
                    .weight(1f)
            )
            chatCard.chat.lastMessage?.let {
                Text(
                    text = timeMillisConverter(it.timeStamp),
                    fontSize = 16.sp,
                    maxLines = 1,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier
                        .padding(vertical = 4.dp)
                )
            }
        }

        Row {
            Text(
                text = lastMessage,
                fontSize = 16.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier
                    .padding(end = 16.dp)
                    .weight(1f)
            )

            if (chatCard.missingMessages != null && chatCard.missingMessages!! > 0) {
                Surface(shape = CircleShape) {
                    Box(
                        modifier = Modifier
                            .background(Red)
                            .size(28.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = chatCard.missingMessages.toString(),
                            color = Color.White,
                            textAlign = TextAlign.Center,
                        )
                    }
                }
            } else {
                Box(modifier = Modifier.size(28.dp))
            }
        }
    }
}