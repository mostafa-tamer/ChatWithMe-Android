package com.mostafatamer.chatwithme.viewModels

import androidx.lifecycle.ViewModel
import com.mostafatamer.chatwithme.network.entity.authenticationDto.AuthenticationRequest
import com.mostafatamer.chatwithme.network.repository.UserRepository
import com.mostafatamer.chatwithme.static.AppUser
import com.mostafatamer.chatwithme.static.RetrofitSingleton
import com.mostafatamer.chatwithme.static.StompClientSingleton

class LoginViewModel(private val userRepository: UserRepository) : ViewModel() {

    fun login(
        username: String,
        password: String,
        firebaseToken: String,
        success: (succeed: Boolean) -> Unit,
    ) {
        userRepository.login(AuthenticationRequest(username, password, firebaseToken))
            .setOnSuccess { apiResponse ->

                success.invoke(apiResponse.data != null)

                apiResponse.data?.let { authenticationResponse ->
                    AppUser.getInstance(authenticationResponse.user)
                    RetrofitSingleton.getRetrofitInstance(authenticationResponse.token)
                    StompClientSingleton.createInstance(authenticationResponse.token)
                }
            }.execute()
    }
}