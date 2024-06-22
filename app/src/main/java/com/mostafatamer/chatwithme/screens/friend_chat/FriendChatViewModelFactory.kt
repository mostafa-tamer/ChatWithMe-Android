package com.mostafatamer.chatwithme.screens.friend_chat

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.mostafatamer.chatwithme.network.entity.dto.ChatDto
import com.mostafatamer.chatwithme.network.entity.dto.User
import com.mostafatamer.chatwithme.network.repository.ChatRepository
import com.mostafatamer.chatwithme.screens.friend_chat.view_model.FriendChatViewModel
import com.mostafatamer.chatwithme.services.StompService
import com.mostafatamer.chatwithme.utils.SharedPreferencesHelper
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import javax.inject.Named

class FriendChatViewModelFactory @AssistedInject constructor(
    private val chatRepository: ChatRepository,
    private val stompService: StompService,
    @Named("friendship_chat_hub_shared_preferences") private val sharedPreferencesHelper: SharedPreferencesHelper,
    private val user: User,
    @Assisted private val chatDto: ChatDto
) : ViewModelProvider.Factory {

    @AssistedFactory
    interface Factory {
        fun create(chatDto: ChatDto): FriendChatViewModelFactory
    }

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(FriendChatViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return FriendChatViewModel(chatRepository, stompService, sharedPreferencesHelper, chatDto, user) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
