package com.mostafatamer.chatwithme.viewModels

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.lifecycle.ViewModel
import com.mostafatamer.chatwithme.enumeration.SharedPreferences
import com.mostafatamer.chatwithme.helper.SharedPreferencesHelper
import com.mostafatamer.chatwithme.enumeration.WebSocketPaths
import com.mostafatamer.chatwithme.network.entity.dto.ChatDto
import com.mostafatamer.chatwithme.network.entity.dto.MessageDto
import com.mostafatamer.chatwithme.network.repository.ChatRepository
import com.mostafatamer.chatwithme.services.StompService
import com.mostafatamer.chatwithme.static.AppUser
import com.mostafatamer.chatwithme.static.JsonConverter
import com.mostafatamer.chatwithme.viewModels.abstract.StompConnection
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter


class FriendChatViewModel(
    private val chatRepository: ChatRepository,
    private val stompService: StompService,
    private val sharedPreferencesHelper: SharedPreferencesHelper,
    val chatDto: ChatDto,
) : ViewModel(), StompConnection {

    init {
        ensureStompConnected(stompService)
    }

    override fun cleanUp() {
        stompService.disconnect()
    }

    private lateinit var onNewMessage: () -> Unit


    val messages = mutableStateListOf<MessageDto>()
    private val messagesSet = mutableSetOf<MessageDto>()

    fun sendMessage(message: String) {
        val newMessage = MessageDto(
            message = message,
            senderUsername = AppUser.getInstance().username,
            timeStamp = System.currentTimeMillis()
        )

        val topic = "${WebSocketPaths.SendMessageRout.path}/${chatDto.tag}"

        stompService.send(topic, newMessage)
    }

    fun observeAndLoadChat() {
        val topic = "${WebSocketPaths.SendMessageToChatMessageBroker.path}/${chatDto.tag}"

        stompService.topicListener(
            topic, onSubscribe = {
                loadChat()
            }
        ) {
            val messageDto = JsonConverter.getInstance().fromJson(
                it.payload, MessageDto::class.java
            )

            if (!messagesSet.contains(messageDto)) {
                messages.addSorted(messageDto)//TODO resolve the memory leek
                onNewMessage.invoke()
                sharedPreferencesHelper.setValue(
                    SharedPreferences.FriendChat.lastMessageNumberWithChatTagAndUsername(
                        chatDto.tag,
                        AppUser.getInstance().username
                    ),
                    messageDto.messageNumber!!
                )
                messagesSet.add(messageDto)
            }
        }
    }

    fun timeMillisConverter(timeMillis: Long): String {
        val dateTime = LocalDateTime.ofInstant(
            Instant.ofEpochSecond(timeMillis), ZoneOffset.UTC
        )

        return dateTime.format(
            DateTimeFormatter.ofPattern("hh:mm a")
        )
    }

    private fun loadChat() {
        chatRepository.loadFriendChat(chatDto.tag)
            .setOnSuccess { apiResponse ->
                if (apiResponse.data != null) {
                    val messagesApi = apiResponse.data!!

                    messagesApi.forEach { messages.addSorted(it) }

                    val lastMessage = messagesApi.maxByOrNull { it.messageNumber!! }

                    lastMessage?.let {
                        sharedPreferencesHelper.setValue(
                            SharedPreferences.FriendChat.lastMessageNumberWithChatTagAndUsername(
                                chatDto.tag,
                                AppUser.getInstance().username
                            ), it.messageNumber!!
                        )
                    }

                    onNewMessage.invoke()
                }
            }.execute()
    }

    //TODO CONVERT TO BINARY SEARCH
    private fun SnapshotStateList<MessageDto>.addSorted(item: MessageDto) {

        if (this.isEmpty()) {
            add(item)
            return
        }

        val iterator = this.listIterator()
        while (iterator.hasNext()) {
            if (item.timeStamp < iterator.next().messageNumber!!) break
        }
        add(iterator.nextIndex(), item)
    }

    fun setOnNewMessageReceived(onNewMessage: () -> Unit) {
        this.onNewMessage = onNewMessage
    }

    override fun ensureStompConnected() {
        ensureStompConnected(stompService)
    }
}

