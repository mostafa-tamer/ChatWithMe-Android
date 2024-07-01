package com.mostafatamer.chatwithme.presentation.viewmodels

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mostafatamer.chatwithme.domain.usecase.ChatMessagesUseCase
import com.mostafatamer.chatwithme.domain.usecase.FriendShipUseCase
import com.mostafatamer.chatwithme.domain.model.dto.dto.MessageDto
import com.mostafatamer.chatwithme.presentation.viewmodels.abstract_view_models.StompLifecycleManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class FriendshipChatViewModel @Inject constructor(
    private val chatMessagesUseCase: ChatMessagesUseCase,
    private val friendshipUseCase: FriendShipUseCase,
) : ViewModel() {
    val stompLifecycleManager =
        StompLifecycleManager(chatMessagesUseCase.stompService, friendshipUseCase.stompService)

    val messages get() = chatMessagesUseCase.messages

    val paginationState get() = chatMessagesUseCase.paginationState

    val user get() = chatMessagesUseCase.user

    var isThereNoMessages by mutableStateOf(false)

    val friend get() = chatMessagesUseCase.friend

    var chat
        get() = chatMessagesUseCase.chat
        set(value) {
            chatMessagesUseCase.chat = value
        }


    private fun notifyIfThereIsMessages() {
        isThereNoMessages = messages.isEmpty()
    }

    fun loadMessages() {
        chatMessagesUseCase.loadMessages {
            notifyIfThereIsMessages()
        }
    }

    fun sendMessage(message: String) {
        chatMessagesUseCase.sendMessage(message)
    }

    fun observeMessages(onNewMessage: suspend CoroutineScope.(MessageDto) -> Unit) {
        chatMessagesUseCase.observeMessages { message ->
            viewModelScope.launch {
                onNewMessage.invoke(this, message)
            }
            notifyIfThereIsMessages()
        }
    }

    fun reset() {
        chatMessagesUseCase.reset()
        isThereNoMessages = false
    }

    fun removeFriend(onFriendRemoved: (Boolean) -> Unit) {
        friendshipUseCase.removeFriend(friend, onFriendRemoved)
    }

    fun observeFriendRemovedMe(onFriendRemovedMe: () -> Unit) {
        friendshipUseCase.observeFriendRemovedMe {
            viewModelScope.launch {
                onFriendRemovedMe()
            }
        }
    }
}

