package com.mostafatamer.chatwithme.dependency_injection

import android.app.Application
import com.google.gson.Gson
import com.mostafatamer.chatwithme.enumeration.SharedPreferencesConstants
import com.mostafatamer.chatwithme.network.entity.dto.User
import com.mostafatamer.chatwithme.utils.SharedPreferencesHelper
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn

import dagger.hilt.components.SingletonComponent
import javax.inject.Named
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
object SharedPreferencesModule {

    @Provides
    @Named("friendship_chat_hub_shared_preferences")
    fun provideFriendshipChatHubSharedPreferences(application: Application): SharedPreferencesHelper {
        return SharedPreferencesHelper(
            application,
            SharedPreferencesConstants.FriendshipChatHub.name
        )
    }

    @Provides
    @Singleton
    @Named("login_shared_preferences")
    fun provideLoginSharedPreferences(application: Application): SharedPreferencesHelper {
        return SharedPreferencesHelper(application, SharedPreferencesConstants.Authentication.name)
    }
}
