package com.mostafatamer.chatwithme.presentation.login_screen

import com.google.gson.Gson
import com.mostafatamer.chatwithme.enumeration.SharedPreferencesConstants
import com.mostafatamer.chatwithme.network.entity.authenticationDto.AuthenticationRequest
import com.mostafatamer.chatwithme.network.entity.authenticationDto.AuthenticationResponse
import com.mostafatamer.chatwithme.network.entity.dto.User
import com.mostafatamer.chatwithme.network.repository.AuthenticationRepository
import com.mostafatamer.chatwithme.presentation.abstract_view_models.AuthenticationViewModel

import com.mostafatamer.chatwithme.utils.SharedPreferencesHelper
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import javax.inject.Named

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val authenticationRepository: AuthenticationRepository,
    private val jsonConverter: Gson,
    @Named("login_shared_preferences") private val loginSharedPreferences: SharedPreferencesHelper,
) : AuthenticationViewModel() {


    fun login(
        username: String,
        password: String,
        success: (succeed: Boolean) -> Unit,
    ) {
        firebaseToken?.let { firebaseToken ->
            authenticationRepository.login(AuthenticationRequest(username, password, firebaseToken))
                .setOnSuccess { apiResponse ->
                    apiResponse.data?.let { authenticationResponse ->
                        saveUserCredentials(authenticationResponse)
                    }
                    success.invoke(apiResponse.data != null)
                }.execute()
        }
    }


    private fun saveUserCredentials(authenticationResponse: AuthenticationResponse) {
        loginSharedPreferences.setValue(
            SharedPreferencesConstants.Authentication.USER_TOKEN,
            authenticationResponse.token
        )
        loginSharedPreferences.setValue(
            SharedPreferencesConstants.Authentication.USER,
            jsonConverter.toJson(authenticationResponse.user)
        )
        loginSharedPreferences.setValue(
            SharedPreferencesConstants.Authentication.USER_TOKEN_TIME,
            System.currentTimeMillis()
        )
    }


    fun validateRegisteredUser(onValidate: (succeed: Boolean) -> Unit) {
        val userToken =
            loginSharedPreferences.getString(SharedPreferencesConstants.Authentication.USER_TOKEN)

        if (userToken == null) {    // no user data to validate
            onValidate.invoke(false)
            return
        }

        val userTokenTime =
            loginSharedPreferences.getLong(SharedPreferencesConstants.Authentication.USER_TOKEN_TIME)
                ?: return

        val currentTime = System.currentTimeMillis()
        val ninetyDays = 7_776_000_000

        if (currentTime - userTokenTime < ninetyDays) {
            onValidate.invoke(true)
        }
    }
}