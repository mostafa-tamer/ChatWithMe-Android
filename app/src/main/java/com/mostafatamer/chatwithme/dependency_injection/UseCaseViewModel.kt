package com.mostafatamer.chatwithme.dependency_injection

import com.mostafatamer.chatwithme.data.repository.realtime.StompRepository
import com.mostafatamer.chatwithme.domain.usecase.ChatMessagesUseCase
import com.mostafatamer.chatwithme.domain.usecase.FriendShipUseCase
import com.mostafatamer.chatwithme.domain.usecase.GroupManagementUseCase
import com.mostafatamer.chatwithme.domain.usecase.chat_usecase.FriendshipHubUseCase
import com.mostafatamer.chatwithme.domain.usecase.chat_usecase.GroupHubUseCase
import com.mostafatamer.chatwithme.domain.model.dto.dto.UserDto
import com.mostafatamer.chatwithme.data.repository.restful.ChatRepository
import com.mostafatamer.chatwithme.data.repository.restful.FriendshipRepository
import com.mostafatamer.chatwithme.data.services.StompService
import com.mostafatamer.chatwithme.utils.SharedPreferencesHelper
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Named

@Module
@InstallIn(SingletonComponent::class)
object UseCaseViewModel {

    @Provides
    fun provideFriendshipHubUseCase(
        chatRepository: ChatRepository,
        @Named("friendship_chat_hub_shared_preferences")
        chatSharedPreferences: SharedPreferencesHelper,
        stompRepository: StompRepository,
        user: UserDto,
    ): FriendshipHubUseCase {
        return FriendshipHubUseCase(
            chatRepository,
            chatSharedPreferences,
            stompRepository,
            user
        )
    }

    @Provides
    fun provideChatMessagesUseCase(
        chatRepository: ChatRepository,
        @Named("friendship_chat_hub_shared_preferences")
        sharedPreferencesHelper: SharedPreferencesHelper,
        stompService: StompService,
        user: UserDto,
    ): ChatMessagesUseCase {
        return ChatMessagesUseCase(chatRepository, sharedPreferencesHelper, stompService, user)
    }

    @Provides
    fun provideGroupHubUseCase(
        chatRepository: ChatRepository,
        @Named("friendship_chat_hub_shared_preferences")
        chatSharedPreferences: SharedPreferencesHelper,
        stompRepository: StompRepository,
        user: UserDto,
    ): GroupHubUseCase {
        return GroupHubUseCase(chatRepository, chatSharedPreferences, stompRepository, user)
    }

    @Provides
    fun provideGroupConfigUseCase(
        chatRepository: ChatRepository,
    ): GroupManagementUseCase {
        return GroupManagementUseCase(chatRepository)
    }

    @Provides
    fun provideFriendshipUseCase(
        friendshipRepository: FriendshipRepository,
        stompRepository: StompRepository,
        user: UserDto,
    ): FriendShipUseCase {
        return FriendShipUseCase(friendshipRepository, stompRepository)
    }
}