package com.mostafatamer.chatwithme.presentation.main_screen.sub_screens.friendship_hub

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.mostafatamer.chatwithme.enumeration.SharedPreferencesConstants
import com.mostafatamer.chatwithme.enumeration.WebSocketPaths
import com.mostafatamer.chatwithme.network.entity.dto.Chat
import com.mostafatamer.chatwithme.network.entity.dto.MessageDto
import com.mostafatamer.chatwithme.network.entity.dto.User
import com.mostafatamer.chatwithme.network.firebase.FriendRequest
import com.mostafatamer.chatwithme.network.repository.ChatRepository
import com.mostafatamer.chatwithme.network.repository.FriendshipRepository
import com.mostafatamer.chatwithme.presentation.abstract_view_models.StompViewModel
import com.mostafatamer.chatwithme.presentation.main_screen.sub_screens.friendship_hub.entity.ChatCard
import com.mostafatamer.chatwithme.services.StompService
import com.mostafatamer.chatwithme.utils.SharedPreferencesHelper
import com.mostafatamer.chatwithme.utils.addSorted
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import javax.inject.Named


@HiltViewModel
class FriendshipHubViewModel @Inject constructor(
    private val chatRepository: ChatRepository,
    private val friendshipRepository: FriendshipRepository,
    private val stompService: StompService,
    @Named("friendship_chat_hub_shared_preferences")
    private val chatSharedPreferences: SharedPreferencesHelper,
    @Named("login_shared_preferences")
    private val loginSharedPreferences: SharedPreferencesHelper,
    val user: User,
) : StompViewModel(stompService) {

    private val loadedChats = mutableSetOf<Chat>()
    private val newFriendRequests = mutableSetOf<FriendRequest>()

    val chats = mutableStateListOf<ChatCard>()

    var numberOfFriendRequests by mutableIntStateOf(0)

    fun clear() {
        chats.clear()
        loadedChats.clear()
        newFriendRequests.clear()
        numberOfFriendRequests.countLeadingZeroBits()
        currentPage = 0
        isLoading = false
        hasNextPage = true
    }

    fun observeChatLastMessage() {
        val topic = WebSocketPaths.SendMessageToChatMessageBroker
            .withUsername(user.username)

        stompService.topicListener(
            topic, MessageDto::class.java
        ) { messageDto ->
            onMessageReceived(messageDto)
        }
    }

    private fun onMessageReceived(messageDto: MessageDto) {
        val chatCard = chats.find { it.chat.tag == messageDto.chatTag }!!

        chats.remove(chatCard)

        val newChat = chatCard.chat.copy(lastMessage = messageDto)
        val newChatCard =
            chatCard.copy(chat = newChat, missingMessages = getMissingMessages(newChat))

        chats.addSorted(newChatCard, comparator(newChatCard.chat))
    }

    fun observeNewChatAndLoadChats() {
        val topic = WebSocketPaths.AcceptFriendRequestMessageBroker
            .withUsername(user.username)

        stompService.topicListener(
            topic, Chat::class.java,

            ) { chat ->
            if (!loadedChats.contains(chat)) {
                val missingMessages = getMissingMessages(chat)
                chats.addSorted(ChatCard(chat, missingMessages), comparator(chat))
                loadedChats.add(chat)
            }
        }
    }

    var currentPage = 0
    var hasNextPage = true
    var isLoading by mutableStateOf(false)

    fun loadChats() {
        isLoading = true
        chatRepository.friendshipChat(currentPage, 25)
            .setOnSuccess { apiResponse ->
                apiResponse.data?.let { chatPage ->

                    chatPage.content.forEach { chat ->
                        if (loadedChats.contains(chat)) return@forEach
                        val missingMessages = getMissingMessages(chat)
                        chats.addSorted(ChatCard(chat, missingMessages), comparator(chat))
                        loadedChats.add(chat)
                    }

                    hasNextPage = !chatPage.last
                }
                isLoading = false
            }.execute()
    }

    private fun comparator(chat: Chat): (chat: ChatCard) -> Int = { chatCard ->
        if (chatCard.chat.lastMessage != null && chat.lastMessage != null) {
            chat.lastMessage!!.timeStamp.compareTo(
                chatCard.chat.lastMessage!!.timeStamp
            )
        } else {
            if (chatCard.chat.lastMessage == null && chat.lastMessage != null) 1 else -1
        }
    }


    private fun getMissingMessages(chat: Chat): Long? {
        val lastMessageNumber = chat.lastMessage?.messageNumber
        val lastReadMessageNumber = getLastReadMessageNumber(chat.tag)
//        println(lastMessageNumber)
//        println(lastReadMessageNumber)
//        println("+++++++++++++++++++++++")


        if (lastMessageNumber == null) {
            resetLastMessageNumber(chat.tag)
            return null
        }

        return if (lastReadMessageNumber == null) lastMessageNumber
        else lastMessageNumber - lastReadMessageNumber
    }


    private fun resetLastMessageNumber(chatTag: String) {
        chatSharedPreferences.setValue(
            SharedPreferencesConstants.FriendshipChatHub.lastMessageNumberWithChatTagAndUsername(
                chatTag, user.username
            ),
            null
        )
    }

    private fun getLastReadMessageNumber(chatTag: String): Long? {
        return chatSharedPreferences.getLong(
            SharedPreferencesConstants.FriendshipChatHub.lastMessageNumberWithChatTagAndUsername(
                chatTag, user.username
            )
        )
    }

    private fun newFriendRequest(friendRequest: FriendRequest) {
        if (!newFriendRequests.contains(friendRequest)) {
            numberOfFriendRequests++
        }
    }

    fun observeFriendRequests() {
        val topic =
            WebSocketPaths.SendFriendRequestMessageBroker
                .withUsername(user.username)

        stompService.topicListener(
            topic, FriendRequest::class.java, onSubscribe = {
                loadNumberOfFriendRequests()
            }
        ) { friendRequest ->
            newFriendRequest(friendRequest)
        }
    }

    private fun loadNumberOfFriendRequests() {
        friendshipRepository.numberOfFriendRequests()
            .setOnSuccess { apiResponse ->
                apiResponse.data?.let { numberOfFriendRequests ->
                    this.numberOfFriendRequests += numberOfFriendRequests
                }
            }.execute()
    }

    fun clearUserDataForAutomaticLogin() {
        loginSharedPreferences.setValue(SharedPreferencesConstants.Authentication.USER_TOKEN, null)
        loginSharedPreferences.setValue(SharedPreferencesConstants.Authentication.USER, null)
        loginSharedPreferences.setValue(SharedPreferencesConstants.Authentication.USER_TOKEN_TIME, null)
    }
}