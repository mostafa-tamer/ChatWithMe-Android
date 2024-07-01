package com.mostafatamer.chatwithme.presentation.viewmodels

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mostafatamer.chatwithme.domain.usecase.ChatMessagesUseCase
import com.mostafatamer.chatwithme.domain.usecase.GroupManagementUseCase
import com.mostafatamer.chatwithme.domain.model.dto.dto.MessageDto
import com.mostafatamer.chatwithme.presentation.viewmodels.abstract_view_models.StompLifecycleManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class GroupChatViewModel @Inject constructor(
    private val chatUseCase: ChatMessagesUseCase,
    private val groupConfig: GroupManagementUseCase,
) : ViewModel() {
    val stompLifecycleManager = StompLifecycleManager(chatUseCase.stompService)

    val messages get() = chatUseCase.messages

    val paginationState get() = chatUseCase.paginationState

    val user get() = chatUseCase.user

    var chat
        get() = groupConfig.chat
        set(value) {
            groupConfig.chat = value
            chatUseCase.chat = value
        }

    var isThereNoMessages by mutableStateOf(false)

    private fun notifyIfThereIsMessages() {
        isThereNoMessages = messages.isEmpty()
    }

    fun loadMessages() {
        chatUseCase.loadMessages {
            notifyIfThereIsMessages()
        }
    }

    fun sendMessage(message: String) {
        chatUseCase.sendMessage(message)
    }

    fun observeMessages(onNewMessage: suspend CoroutineScope.(MessageDto) -> Unit) {
        chatUseCase.observeMessages { message ->
            viewModelScope.launch {
                onNewMessage.invoke(this, message)
            }
            notifyIfThereIsMessages()
        }
    }

    fun reset() {
        chatUseCase.reset()
        isThereNoMessages = false
    }

    fun addMember(username: String, onMemberAdded: (Boolean) -> Unit) {
        groupConfig.addMember(username, onMemberAdded)
    }

    fun leaveGroup(onGroupLeft: (Boolean) -> Unit) {
        groupConfig.leaveGroup(onGroupLeft)
    }
}


