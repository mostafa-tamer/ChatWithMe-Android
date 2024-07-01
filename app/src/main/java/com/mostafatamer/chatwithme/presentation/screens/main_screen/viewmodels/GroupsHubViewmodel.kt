package com.mostafatamer.chatwithme.presentation.screens.main_screen.viewmodels

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.mostafatamer.chatwithme.domain.usecase.GroupManagementUseCase
import com.mostafatamer.chatwithme.domain.usecase.chat_usecase.GroupHubUseCase
import com.mostafatamer.chatwithme.presentation.viewmodels.abstract_view_models.StompLifecycleManager
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class GroupsHubViewmodel @Inject constructor(
    private val chatUseCase: GroupHubUseCase,
    private val groupManagementUseCase: GroupManagementUseCase,
) : ViewModel() {

    val stompLifecycleManager = StompLifecycleManager(chatUseCase.stompService)

    val chats get() = chatUseCase.chats

    val paginationHelper get() = chatUseCase.paginationState

    var isThereNoGroupsChat by mutableStateOf(false)

    private fun updateIfThereIsNoGroupsState() {
        isThereNoGroupsChat = chats.isEmpty()
    }

    fun observeNewChats() {
        chatUseCase.observeNewGroupChat {
            updateIfThereIsNoGroupsState()
        }
    }

    fun loadChats() {
        chatUseCase.loadChats {
            updateIfThereIsNoGroupsState()
        }
    }

    fun observeIfChatReceivedNewMessage() {
        chatUseCase.observeIfChatReceivedNewMessage()
    }

    fun reset() {
        chatUseCase.reset()
        isThereNoGroupsChat = false
    }

    fun createGroup(groupName: String, onCreateGroup: (Boolean) -> Unit) {
        groupManagementUseCase.createGroup(groupName, onCreateGroup)
    }
}
