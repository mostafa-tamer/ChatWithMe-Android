package com.mostafatamer.chatwithme.viewModels

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mostafatamer.chatwithme.enumeration.SharedPreferences
import com.mostafatamer.chatwithme.enumeration.WebSocketPaths
import com.mostafatamer.chatwithme.network.entity.dto.ChatDto
import com.mostafatamer.chatwithme.network.entity.dto.ChatLastMessageNumberDto
import com.mostafatamer.chatwithme.network.entity.dto.MessageDto
import com.mostafatamer.chatwithme.network.firebase.FriendRequest
import com.mostafatamer.chatwithme.network.repository.ChatRepository
import com.mostafatamer.chatwithme.network.repository.FriendshipRepository
import com.mostafatamer.chatwithme.services.StompService
import com.mostafatamer.chatwithme.static.UserSingleton
import com.mostafatamer.chatwithme.static.JsonConverter
import com.mostafatamer.chatwithme.utils.SharedPreferencesHelper
import kotlinx.coroutines.launch

class ChatsViewModel(
    private val chatRepository: ChatRepository,
    private val friendshipRepository: FriendshipRepository,
    private val stompService: StompService,
    private val chatSharedPreferences: SharedPreferencesHelper,
    private val loginSharedPreferences: SharedPreferencesHelper,
) : ViewModel()
{
    //chat tag -> lastMessageNumber to missingMessages
    private val lastReadMessagesMap = mutableMapOf<String, Pair<Long, Long>>()
    var numberOfFriendRequests by mutableIntStateOf(0)
        private set
    private val newFriendRequests = mutableSetOf<FriendRequest>()
    private val observedChats = mutableSetOf<ChatDto>()

    val chats = mutableStateListOf<ChatWithMissingMessages>()



    private fun observeChatsLastMessage() {
        chats.forEachIndexed { index, chatWithMissingMessages ->
            observeChatLastMessage(chatWithMissingMessages, index) {
                loadChatsLastMessageNumber()
            }
        }
    }

    private fun observeChatLastMessage(
        chatWithMissingMessages: ChatWithMissingMessages,
        index: Int,
        onSubscribe: () -> Unit = {},
    ) {
        val topic = WebSocketPaths.SendMessageToChatMessageBroker
            .pathVariable(chatWithMissingMessages.chat.tag)

        stompService.topicListener(
            topic, onSubscribe = {
                onSubscribe()
            }
        ) {
            val messageDto = JsonConverter.getInstance()
                .fromJson(it.payload, MessageDto::class.java)

            if (lastReadMessagesMap[chatWithMissingMessages.chat.tag] != null) {
                val missingMessage = (messageDto.messageNumber!!
                        - lastReadMessagesMap[chatWithMissingMessages.chat.tag]!!.first
                        + lastReadMessagesMap[chatWithMissingMessages.chat.tag]!!.second)

                updateMissingMessages(index, missingMessage)
            } else {
                val missingMessages = getMissingMessages(
                    chatWithMissingMessages.chat.tag,
                    messageDto.messageNumber!!
                )

                lastReadMessagesMap[chatWithMissingMessages.chat.tag] =
                    messageDto.messageNumber!! to missingMessages

                updateMissingMessages(index, missingMessages)
            }
        }
    }

    private fun loadChatsLastMessageNumber() {
        chatRepository.loadChatsLastMessageNumber(
            chats.map { it.chat.tag }
        ).setOnSuccess { apiResponse ->
            apiResponse.data?.forEachIndexed { index, it ->
                checkLastReadMessageAndUpdateMissingMessages(it, index)
            }
        }.execute()
    }

    private fun checkLastReadMessageAndUpdateMissingMessages(
        it: ChatLastMessageNumberDto,
        index: Int,
    ) {
        viewModelScope.launch {
            val missingMessages = getMissingMessages(it.tag, it.lastMessageNumber)

            if (lastReadMessagesMap[it.tag] == null) {
                lastReadMessagesMap[it.tag] = it.lastMessageNumber to missingMessages
                updateMissingMessages(index, missingMessages)
            }
        }
    }

    private fun getMissingMessages(chatTag: String, lastMessageNumber: Long): Long {
        val lastReadMessageNumber =
            chatSharedPreferences.getLong(
                SharedPreferences.FriendChat.lastMessageNumberWithChatTagAndUsername(
                    chatTag,
                    UserSingleton.getInstance().username
                )
            )

        val missingMessages =
            if (lastReadMessageNumber == null) lastMessageNumber
            else lastMessageNumber - lastReadMessageNumber

        return missingMessages
    }

    private fun updateMissingMessages(index: Int, missingMessage: Long) {
        chats[index] = chats[index].copy(missingMessages = missingMessage)
    }

    fun loadAllChatsAndObserveChatsForNewMessageAndLoadLastMessageNumberOfEachChat() {
        chatRepository.allFriendsChat()
            .setOnSuccess { apiResponse ->
                apiResponse.data?.let { chatResponse ->
                    chats.clear()
                    lastReadMessagesMap.clear()

                    chatResponse.forEach {chat->
                        repeat(20){ chats.add(ChatWithMissingMessages(chat)) }
                    }

                    observeChatsLastMessage()
                }
            }.execute()
    }



    fun observeFriendRequests() {
        val topic =
            WebSocketPaths.SendFriendRequestMessageBroker
                .pathVariable(UserSingleton.getInstance().username)

        stompService.topicListener(
            topic,
            onSubscribe = {
                loadNumberOfFriendRequests()
            }
        ) {
            val friendRequest = JsonConverter.getInstance()
                .fromJson(it.payload, FriendRequest::class.java)
//            println(it.payload)
            if (!newFriendRequests.contains(friendRequest)) {
                this.numberOfFriendRequests++
            }
        }
    }

    private fun loadNumberOfFriendRequests() {
        friendshipRepository.numberOfFriendRequests()
            .setOnSuccess { apiResponse ->
                apiResponse.data?.let {
                    numberOfFriendRequests += it
                }
            }.execute()
    }

    fun observeNewChat() {
        val topic = WebSocketPaths.AcceptFriendRequestMessageBroker
            .pathVariable(UserSingleton.getInstance().username)
        println(topic)
        stompService.topicListener(
            topic,
            onSubscribe = {
                println("sub")
            }
        ) {
            val chatDto = JsonConverter.getInstance()
                .fromJson(it.payload, ChatDto::class.java)

            if (!observedChats.contains(chatDto)) {
                val chatWithMissingMessages = ChatWithMissingMessages(chatDto, 0)

                chats.add(chatWithMissingMessages)
                observedChats.add(chatDto)

                val index = chats.indexOf(chatWithMissingMessages)
                observeChatLastMessage(chatWithMissingMessages, index)
            }
        }
    }

    private fun loadChatLastMessageNumber(chat: ChatDto, index: Int) {
        chatRepository.loadChatLastMessageNumber(
            chat.tag
        ).setOnSuccess { apiResponse ->
            println(apiResponse)
            apiResponse.data?.let {
                val lastMessageNumber = it
                checkLastReadMessageAndUpdateMissingMessages(lastMessageNumber, index)
            }
        }.execute()
    }

    fun clearUserDataForAutomaticLogin() {
        loginSharedPreferences.setValue(SharedPreferences.Login.USER_TOKEN, null)
        loginSharedPreferences.setValue(SharedPreferences.Login.USER, null)
        loginSharedPreferences.setValue(SharedPreferences.Login.USER_TOKEN_TIME, null)
    }

    data class ChatWithMissingMessages(
        val chat: ChatDto,
        var missingMessages: Long? = null,
    )
}