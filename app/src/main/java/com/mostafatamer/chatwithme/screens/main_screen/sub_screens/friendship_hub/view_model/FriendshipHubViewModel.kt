package com.mostafatamer.chatwithme.screens.main_screen.sub_screens.friendship_hub.view_model

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.mostafatamer.chatwithme.enumeration.SharedPreferencesConstants
import com.mostafatamer.chatwithme.enumeration.WebSocketPaths
import com.mostafatamer.chatwithme.network.entity.dto.ChatDto
import com.mostafatamer.chatwithme.network.entity.dto.MessageDto
import com.mostafatamer.chatwithme.network.entity.dto.User
import com.mostafatamer.chatwithme.network.firebase.FriendRequest
import com.mostafatamer.chatwithme.network.repository.ChatRepository
import com.mostafatamer.chatwithme.network.repository.FriendshipRepository
import com.mostafatamer.chatwithme.screens.main_screen.sub_screens.friendship_hub.entity.ChatCard
import com.mostafatamer.chatwithme.services.StompService
import com.mostafatamer.chatwithme.utils.SharedPreferencesHelper
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
    private val user: User,
) : ViewModel() {
    private val loadChatManager = LoadChatManager(this)
    private val friendRequests = FriendRequests(this)

    val chats = mutableStateListOf<ChatCard>()
    var numberOfFriendRequests by mutableIntStateOf(0)

    fun observeChatLastMessage() {
        val topic = WebSocketPaths.SendMessageToChatMessageBroker
            .withUsername(user.username)

        stompService.topicListener(
            topic, MessageDto::class.java
        ) { messageDto ->
            loadChatManager.onMessageReceived(messageDto)
        }
    }

    var page = 0
    var size = 2
    var isLoading = mutableStateOf(false)

    fun loadChats() {
        if (isLoading.value) {

            return
        }
        println("page: $page")
        isLoading.value = true
        chatRepository.allFriendshipChat(page, size)
            .setOnSuccess { apiResponse ->
                println(apiResponse.data)
                apiResponse.data?.let { chatResponse ->
                    loadChatManager.addChats(chatResponse.content)
                    page++
                }
                isLoading.value = false
            }.setOnServiceInteractionFail {
                println(it.message)
            }
            .execute()
    }

    fun observeNewChatAndLoadChats() {
        val topic = WebSocketPaths.AcceptFriendRequestMessageBroker
            .withUsername(user.username)

        stompService.topicListener(
            topic, ChatDto::class.java,
            onSubscribe = {
//                loadChats()
            }
        ) { chatDto ->
            loadChatManager.addChat(chatDto)
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
            friendRequests.newFriendRequest(friendRequest)
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
        loginSharedPreferences.setValue(SharedPreferencesConstants.Login.USER_TOKEN, null)
        loginSharedPreferences.setValue(SharedPreferencesConstants.Login.USER, null)
        loginSharedPreferences.setValue(SharedPreferencesConstants.Login.USER_TOKEN_TIME, null)
    }

    private class LoadChatManager(
        private val friendChatViewModel: FriendshipHubViewModel,
    ) {

        private val loadedChats: MutableSet<ChatDto> = mutableSetOf()

        fun addChat(chatDto: ChatDto) {
            if (!loadedChats.contains(chatDto)) {
                val missingMessages = getMissingMessages(chatDto)
                val chatCard = ChatCard(chatDto, missingMessages)

                friendChatViewModel.chats.add(chatCard)
                loadedChats.add(chatDto)
            }
        }

        private fun getMissingMessages(chatDto: ChatDto): Long? {
            val lastMessageNumber = chatDto.lastMessage?.messageNumber
            val lastReadMessageNumber = getLastReadMessageNumber(chatDto.tag)
//        println(lastMessageNumber)
//        println(lastReadMessageNumber)
//        println("+++++++++++++++++++++++")


            if (lastMessageNumber == null) {
                resetLastMessageNumber(chatDto.tag)
                return null
            }

            return if (lastReadMessageNumber == null) lastMessageNumber
            else lastMessageNumber - lastReadMessageNumber
        }

        private fun resetLastMessageNumber(chatTag: String) {
            friendChatViewModel.chatSharedPreferences.setValue(
                SharedPreferencesConstants.FriendshipChatHub.lastMessageNumberWithChatTagAndUsername(
                    chatTag, friendChatViewModel.user.username
                ),
                null
            )
        }

        private fun getLastReadMessageNumber(chatTag: String): Long? {
            return friendChatViewModel.chatSharedPreferences.getLong(
                SharedPreferencesConstants.FriendshipChatHub.lastMessageNumberWithChatTagAndUsername(
                    chatTag, friendChatViewModel.user.username
                )
            )
        }

        fun addChats(chatResponse: List<ChatDto>) {
            val newChats = chatResponse.map { chatDto ->
                val missingMessages = getMissingMessages(chatDto)
                ChatCard(chatDto, missingMessages)
            }
//        friendChatViewModel.chats.clear()
            friendChatViewModel.chats.addAll(newChats)
        }

        fun onMessageReceived(messageDto: MessageDto) {
            val chatCard = friendChatViewModel.chats.find { it.chat.tag == messageDto.chatTag }!!
            val index = friendChatViewModel.chats.indexOf(chatCard)
            val newChat = chatCard.chat.copy(lastMessage = messageDto)
            friendChatViewModel.chats[index] = chatCard.copy(
                chat = newChat,
                missingMessages = getMissingMessages(newChat)
            )
        }
    }

    private class FriendRequests(private val friendChatViewModel: FriendshipHubViewModel) {
        private val newFriendRequests = mutableSetOf<FriendRequest>()

        fun newFriendRequest(friendRequest: FriendRequest) {
            if (!newFriendRequests.contains(friendRequest)) {
                friendChatViewModel.numberOfFriendRequests++
            }
        }
    }
}