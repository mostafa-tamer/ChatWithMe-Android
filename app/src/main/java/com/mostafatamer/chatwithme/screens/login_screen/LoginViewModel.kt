package com.mostafatamer.chatwithme.screens.login_screen

import com.mostafatamer.chatwithme.AppDependencies
import com.mostafatamer.chatwithme.enumeration.SharedPreferencesConstants
import com.mostafatamer.chatwithme.network.entity.authenticationDto.AuthenticationRequest
import com.mostafatamer.chatwithme.network.entity.authenticationDto.AuthenticationResponse
import com.mostafatamer.chatwithme.network.entity.dto.User
import com.mostafatamer.chatwithme.network.repository.AuthenticationRepository
import com.mostafatamer.chatwithme.screens.abstract_view_models.AuthenticationViewModel
import com.mostafatamer.chatwithme.utils.JsonConverter
import com.mostafatamer.chatwithme.utils.SharedPreferencesHelper
import com.mostafatamer.chatwithme.utils.getRetrofit
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import javax.inject.Named

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val authenticationRepository: AuthenticationRepository,
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

//                        prepareNeededObjects(
//                            authenticationResponse.user,
//                            authenticationResponse.token
//                        )
                    }
                    success.invoke(apiResponse.data != null)
                }.execute()
        }
    }

//    private fun prepareNeededObjects(user: User, token: String) {
//        appDependencies.userToken = token
//        appDependencies.user = user
//        appDependencies.retrofit = getRetrofit(token)
//    }

    private fun saveUserCredentials(authenticationResponse: AuthenticationResponse) {
        loginSharedPreferences.setValue(
            SharedPreferencesConstants.Login.USER_TOKEN,
            authenticationResponse.token
        )
        loginSharedPreferences.setValue(
            SharedPreferencesConstants.Login.USER,
            JsonConverter.toJson(authenticationResponse.user)
        )
        loginSharedPreferences.setValue(
            SharedPreferencesConstants.Login.USER_TOKEN_TIME,
            System.currentTimeMillis()
        )
    }


    fun validateRegisteredUser(onValidate: (succeed: Boolean) -> Unit) {
        val userToken =
            loginSharedPreferences.getString(SharedPreferencesConstants.Login.USER_TOKEN)

        if (userToken == null) {    // no user data to validate
            onValidate.invoke(false)
            return
        }

        val userTokenTime =
            loginSharedPreferences.getLong(SharedPreferencesConstants.Login.USER_TOKEN_TIME)
                ?: return
        val userJson =
            loginSharedPreferences.getString(SharedPreferencesConstants.Login.USER) ?: return


        val currentTime = System.currentTimeMillis()
        val ninetyDays = 7_776_000_000

        if (currentTime - userTokenTime < ninetyDays) {
            val user = JsonConverter.fromJson(userJson, User::class.java)!!

//            prepareNeededObjects(user, userToken)

            onValidate.invoke(true)
        }
    }
}