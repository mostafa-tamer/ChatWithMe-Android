package com.mostafatamer.chatwithme.dependency_injection

import com.mostafatamer.chatwithme.network.repository.AuthenticationRepository
import com.mostafatamer.chatwithme.network.repository.ChatRepository
import com.mostafatamer.chatwithme.network.repository.FriendshipRepository
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
    fun provideChatRepository(retrofit: Retrofit): ChatRepository {
        return ChatRepository(retrofit)
    }

    @Provides
    fun provideFriendshipRepository(retrofit: Retrofit): FriendshipRepository {
        return FriendshipRepository(retrofit)
    }
}
