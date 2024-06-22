package com.mostafatamer.chatwithme.dependency_injection

import android.app.Application
import androidx.compose.runtime.mutableStateOf
import com.mostafatamer.chatwithme.enumeration.SharedPreferencesConstants
import com.mostafatamer.chatwithme.network.repository.ChatRepository
import com.mostafatamer.chatwithme.screens.friend_chat.view_model.FriendChatViewModel
import com.mostafatamer.chatwithme.utils.SharedPreferencesHelper
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Named
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    @Named("token")
    fun provideToken(application: Application): String {
        val sharedPreferencesConstantsHelper =
            SharedPreferencesHelper(application, SharedPreferencesConstants.Login.name)
        return sharedPreferencesConstantsHelper.getString(SharedPreferencesConstants.Login.USER_TOKEN)!!
    }
}