package com.mostafatamer.chatwithme.dependency_injection

import android.app.Application
import com.google.gson.Gson
import com.mostafatamer.chatwithme.sealed.SharedPreferencesConstants
import com.mostafatamer.chatwithme.domain.model.dto.dto.UserDto
import com.mostafatamer.chatwithme.utils.SharedPreferencesHelper
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Named



@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Named("token")
    fun provideToken(application: Application): String {
        val sharedPreferencesConstantsHelper =
            SharedPreferencesHelper(application, SharedPreferencesConstants.Authentication.name)
        return sharedPreferencesConstantsHelper.getString(SharedPreferencesConstants.Authentication.USER_TOKEN)!!
    }

    @Provides
    fun user(
        @Named("login_shared_preferences")
        sharedPreferencesHelper: SharedPreferencesHelper,
        gson: Gson,
    ): UserDto {
        val userJson =
            sharedPreferencesHelper.getString(SharedPreferencesConstants.Authentication.USER)!!

        return gson.fromJson(userJson, UserDto::class.java)
    }
}