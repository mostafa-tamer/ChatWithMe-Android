package com.mostafatamer.chatwithme.presentation.screens.main_screen.viewmodels

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.mostafatamer.chatwithme.domain.usecase.FriendShipUseCase
import com.mostafatamer.chatwithme.domain.usecase.chat_usecase.FriendshipHubUseCase
import com.mostafatamer.chatwithme.sealed.SharedPreferencesConstants
import com.mostafatamer.chatwithme.domain.model.dto.firebase.FriendRequest
import com.mostafatamer.chatwithme.presentation.viewmodels.abstract_view_models.StompLifecycleManager
import com.mostafatamer.chatwithme.utils.SharedPreferencesHelper
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import javax.inject.Named


@HiltViewModel
class FriendshipHubViewModel @Inject constructor(
    @Named("login_shared_preferences")
    private val loginSharedPreferences: SharedPreferencesHelper,
    private val friendshipHubUseCase: FriendshipHubUseCase,
    private val friendshipUseCase: FriendShipUseCase,
) : ViewModel() {

    private val newFriendRequests = mutableSetOf<FriendRequest>()

    val user get() = friendshipHubUseCase.user

    val paginationState get() = friendshipHubUseCase.paginationState

    val chats get() = friendshipHubUseCase.chats

    val stompLifecycleManager =
        StompLifecycleManager(friendshipHubUseCase.stompService, friendshipUseCase.stompService)

    var isThereNoChats by mutableStateOf(false)

    var numberOfFriendRequests by mutableIntStateOf(0)

    fun reset() {
        chats.clear()
//        loadedChats.clear()
        newFriendRequests.clear()
        numberOfFriendRequests = 0
        friendshipHubUseCase.reset()

//        paginationState.currentPage = 0
//        paginationState.isLoading = false
//        paginationState.hasMorePages = true
    }

    fun observeNewChats() {
        friendshipHubUseCase.observeNewFriendshipChats {
            updateIfThereIsNoChatsState()
        }
    }

    fun observeIfChatReceivedNewMessage() {
        friendshipHubUseCase.observeIfChatReceivedNewMessage()
    }

    fun observeFriendRequests() {
//        val topic =
//            WebSocketPaths.SendFriendRequestMessageBroker
//                .withUsername(user.username)
//
//        stompService.topicListener(
//            topic, FriendRequest::class.java, onSubscribe = {
//                loadNumberOfFriendRequests()
//            }
//        ) { friendRequest ->
//            newFriendRequest(friendRequest)
//        }
    }

    private fun updateIfThereIsNoChatsState() {
        isThereNoChats = chats.isEmpty()
    }

    fun clearUserDataForAutomaticLogin() {
        loginSharedPreferences.setValue(SharedPreferencesConstants.Authentication.USER_TOKEN, null)
        loginSharedPreferences.setValue(SharedPreferencesConstants.Authentication.USER, null)
        loginSharedPreferences.setValue(
            SharedPreferencesConstants.Authentication.USER_TOKEN_TIME,
            null
        )
    }

    private fun newFriendRequest(friendRequest: FriendRequest) {
//        if (!newFriendRequests.contains(friendRequest)) {
//            numberOfFriendRequests++
//        }
    }

    private fun loadNumberOfFriendRequests() {
//        friendshipRepository.numberOfFriendRequests()
//            .setOnSuccess { apiResponse ->
//                apiResponse.data?.let { numberOfFriendRequests ->
//                    this.numberOfFriendRequests += numberOfFriendRequests
//                }
//            }.execute()
    }

    fun loadChats() {
        friendshipHubUseCase.loadChats {
            updateIfThereIsNoChatsState()
        }
    }

    fun observeFriendRemovedMe() {
        println("observeFriendRemovedMe")
        friendshipUseCase.observeFriendRemovedMe {
            println(it)
            friendshipHubUseCase.removeChat(it)
        }
    }


//    fun observeChatLastMessage() {
//        val topic = WebSocketPaths.SendMessageToChatMessageBroker
//            .withUsername(user.username)
//
//        stompService.topicListener(
//            topic, MessageDto::class.java
//        ) { messageDto ->
//            val chatCard = chats.find { it.chat.tag == messageDto.chatTag }!!
//
//            chats.remove(chatCard)
//
//            val newChat = chatCard.chat.copy(lastMessage = messageDto)
//            val newChatCard =
//                chatCard.copy(chat = newChat, missingMessages = getMissingMessages(newChat))
//
//            chats.addSorted(newChatCard, comparator(newChatCard.chat))
//        }
//    }


//    fun observeNewChats() {
//        val topic = WebSocketPaths.AcceptFriendRequestMessageBroker
//            .withUsername(user.username)
//
//        stompService.topicListener(
//            topic, Chat::class.java,
//
//            ) { chat ->
//            if (!loadedChats.contains(chat)) {
//                val missingMessages = getMissingMessages(chat)
//                chats.addSorted(ChatCard(chat, missingMessages), comparator(chat))
//                loadedChats.add(chat)
//                notifyFriendsInList()
//            }
//        }
//    }

//    fun loadChats() {
//        paginationState.isLoading = true
//        chatRepository.friendshipChats(paginationState.currentPage, 25)
//            .setOnSuccess { apiResponse ->
//                apiResponse.data?.let { chatPage ->
//
//                    chatPage.content.forEach { chat ->
//                        if (loadedChats.contains(chat)) return@forEach
//                        val missingMessages = getMissingMessages(chat)
//                        chats.addSorted(ChatCard(chat, missingMessages), comparator(chat))
//                        loadedChats.add(chat)
//                    }
//
//                    paginationState.hasMorePages = !chatPage.last
//                    notifyFriendsInList()
//                }
//                paginationState.isLoading = false
//            }.execute()
//    }


//    private fun comparator(chat: Chat): (chat: ChatCard) -> Int = { chatCard ->
//        if (chatCard.chat.lastMessage != null && chat.lastMessage != null) {
//            chat.lastMessage!!.timeStamp.compareTo(
//                chatCard.chat.lastMessage!!.timeStamp
//            )
//        } else {
//            if (chatCard.chat.lastMessage == null && chat.lastMessage != null) 1 else -1
//        }
//    }

//    private fun getMissingMessages(chat: Chat): Long? {
//        val lastMessageNumber = chat.lastMessage?.messageNumber
//        val lastReadMessageNumber = getLastReadMessageNumber(chat.tag)
////        println(lastMessageNumber)
////        println(lastReadMessageNumber)
////        println("+++++++++++++++++++++++")
//
//
//        if (lastMessageNumber == null) {
//            resetLastMessageNumber(chat.tag)
//            return null
//        }
//
//        return if (lastReadMessageNumber == null) lastMessageNumber
//        else lastMessageNumber - lastReadMessageNumber
//    }

//    private fun resetLastMessageNumber(chatTag: String) {
//        chatSharedPreferences.setValue(
//            SharedPreferencesConstants.FriendshipChatHub.lastMessageNumberWithChatTagAndUsername(
//                chatTag, user.username
//            ),
//            null
//        )
//    }
//
//    private fun getLastReadMessageNumber(chatTag: String): Long? {
//        return chatSharedPreferences.getLong(
//            SharedPreferencesConstants.FriendshipChatHub.lastMessageNumberWithChatTagAndUsername(
//                chatTag, user.username
//            )
//        )
//    }


}