package com.mostafatamer.chatwithme.dependency_injection

import com.mostafatamer.chatwithme.data.repository.realtime.StompRepository
import com.mostafatamer.chatwithme.domain.model.dto.dto.UserDto
import com.mostafatamer.chatwithme.data.repository.restful.AuthenticationRepository
import com.mostafatamer.chatwithme.data.repository.restful.ChatRepository
import com.mostafatamer.chatwithme.data.repository.restful.FriendshipRepository
import com.mostafatamer.chatwithme.data.services.StompService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import javax.inject.Named

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {

    @Provides
    fun provideAuthenticationRepository(@Named("retrofit_no_headers") retrofit: Retrofit): AuthenticationRepository {
        return AuthenticationRepository(retrofit)
    }

    @Provides
    fun provideChatRepository(@Named("retrofit") retrofit: Retrofit): ChatRepository {
        return ChatRepository(retrofit)
    }

    @Provides
    fun provideFriendshipRepository(@Named("retrofit") retrofit: Retrofit): FriendshipRepository {
        return FriendshipRepository(retrofit)
    }

    @Provides
    fun provideStompChatRepository(userDto: UserDto, stompService: StompService): StompRepository {
        return StompRepository(userDto, stompService)
    }
}
