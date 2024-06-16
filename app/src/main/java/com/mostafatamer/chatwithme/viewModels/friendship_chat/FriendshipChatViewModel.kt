package com.mostafatamer.chatwithme.viewModels.friendship_chat

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mostafatamer.chatwithme.Singleton.UserSingleton
import com.mostafatamer.chatwithme.enumeration.SharedPreferences
import com.mostafatamer.chatwithme.enumeration.WebSocketPaths
import com.mostafatamer.chatwithme.network.entity.dto.ChatDto
import com.mostafatamer.chatwithme.network.entity.dto.ChatLastMessageNumberDto
import com.mostafatamer.chatwithme.network.entity.dto.MessageDto
import com.mostafatamer.chatwithme.network.firebase.FriendRequest
import com.mostafatamer.chatwithme.network.repository.ChatRepository
import com.mostafatamer.chatwithme.network.repository.FriendshipRepository
import com.mostafatamer.chatwithme.services.StompService
import com.mostafatamer.chatwithme.utils.SharedPreferencesHelper
import kotlinx.coroutines.launch

class FriendshipChatViewModel(
    private val chatRepository: ChatRepository,
    private val friendshipRepository: FriendshipRepository,
    private val stompService: StompService,
    private val chatSharedPreferences: SharedPreferencesHelper,
    private val loginSharedPreferences: SharedPreferencesHelper,
) : ViewModel() {
    //chat tag -> lastMessageNumber to missingMessages
    private val lastReadMessagesMap = mutableMapOf<String, Pair<Long, Long>>()

    var numberOfFriendRequests by mutableIntStateOf(0)
        private set
    private val newFriendRequests = mutableSetOf<FriendRequest>()

    val chats = mutableStateListOf<ChatWithMissingMessages>()

    val loadChatManager = LoadChatManager(this)


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
            .withChatTag(chatWithMissingMessages.chat.tag)

        stompService.topicListener(
            topic, MessageDto::class.java, onSubscribe = {
                onSubscribe()
            }
        ) {

            if (lastReadMessagesMap[chatWithMissingMessages.chat.tag] != null) {
                val missingMessage = (it.messageNumber!!
                        - lastReadMessagesMap[chatWithMissingMessages.chat.tag]!!.first
                        + lastReadMessagesMap[chatWithMissingMessages.chat.tag]!!.second)

                updateMissingMessages(index, missingMessage)
            } else {
                val missingMessages = getMissingMessages(
                    chatWithMissingMessages.chat.tag,
                    it.messageNumber!!
                )

                lastReadMessagesMap[chatWithMissingMessages.chat.tag] =
                    it.messageNumber!! to missingMessages

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


    private fun loadChats() {
        chatRepository.allFriendshipChat()
            .setOnSuccess { apiResponse ->
                apiResponse.data?.let { chatResponse ->
                    val newChats = chatResponse.map { ChatWithMissingMessages(it) }
                    chats.clear()
                    chats.addAll(newChats)
                }
            }.execute()
    }

    fun observeNewChatAndLoadChats() {
        val topic = WebSocketPaths.AcceptFriendRequestMessageBroker
            .withUsername(UserSingleton.getInstance().username)

        stompService.topicListener(
            topic, ChatDto::class.java,
            onSubscribe = {
                loadChats()
            }
        ) { chatDto ->
            loadChatManager.addChat(chatDto)
        }
    }

    fun observeFriendRequests() {
        val topic =
            WebSocketPaths.SendFriendRequestMessageBroker
                .withUsername(UserSingleton.getInstance().username)

        stompService.topicListener(
            topic, FriendRequest::class.java,
            onSubscribe = {
                loadNumberOfFriendRequests()
            }
        ) { friendRequest ->
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