package com.mostafatamer.chatwithme.viewModels

import com.mostafatamer.chatwithme.AppDependencies
import com.mostafatamer.chatwithme.enumeration.SharedPreferences
import com.mostafatamer.chatwithme.network.entity.authenticationDto.AuthenticationRequest
import com.mostafatamer.chatwithme.network.entity.authenticationDto.AuthenticationResponse
import com.mostafatamer.chatwithme.network.entity.dto.User
import com.mostafatamer.chatwithme.network.repository.UserRepository
import com.mostafatamer.chatwithme.utils.JsonConverter
import com.mostafatamer.chatwithme.utils.SharedPreferencesHelper
import com.mostafatamer.chatwithme.utils.getRetrofit

class LoginViewModel(
    private val userRepository: UserRepository,
    private val appDependencies: AppDependencies,
    private val loginSharedPreferences: SharedPreferencesHelper,

    ) : AuthenticationViewModel() {

    fun login(
        username: String,
        password: String,
        success: (succeed: Boolean) -> Unit,
    ) {
        firebaseToken?.let { firebaseToken ->
            userRepository.login(AuthenticationRequest(username, password, firebaseToken))
                .setOnSuccess { apiResponse ->

                    apiResponse.data?.let { authenticationResponse ->
                        prepareNeededObjects(
                            authenticationResponse.user,
                            authenticationResponse.token
                        )
                        saveUserCredentials(authenticationResponse)

                    }
                    success.invoke(apiResponse.data != null)
                }.execute()
        }
    }

    private fun prepareNeededObjects(user: User, token: String) {
        appDependencies.userToken = token
        appDependencies.user = user
        appDependencies.retrofit = getRetrofit(token)
    }

    private fun saveUserCredentials(authenticationResponse: AuthenticationResponse) {
        loginSharedPreferences.setValue(
            SharedPreferences.Login.USER_TOKEN,
            authenticationResponse.token
        )
        loginSharedPreferences.setValue(
            SharedPreferences.Login.USER,
            JsonConverter.toJson(authenticationResponse.user)
        )
        loginSharedPreferences.setValue(
            SharedPreferences.Login.USER_TOKEN_TIME,
            System.currentTimeMillis()
        )
    }


    fun validateRegisteredUser(onValidate: (succeed: Boolean) -> Unit) {
        val userToken =
            loginSharedPreferences.getString(SharedPreferences.Login.USER_TOKEN)

        if (userToken == null) {    // no user data to validate
            onValidate.invoke(false)
            return
        }

        val userTokenTime =
            loginSharedPreferences.getLong(SharedPreferences.Login.USER_TOKEN_TIME) ?: return
        val userJson = loginSharedPreferences.getString(SharedPreferences.Login.USER) ?: return


        val currentTime = System.currentTimeMillis()
        val ninetyDays = 7_776_000_000

        if (currentTime - userTokenTime < ninetyDays) {
            val user = JsonConverter.fromJson(userJson, User::class.java)!!

            prepareNeededObjects(user, userToken)

            onValidate.invoke(true)
        }
    }
}