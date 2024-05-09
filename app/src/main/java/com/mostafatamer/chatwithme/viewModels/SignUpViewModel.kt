package com.mostafatamer.chatwithme.viewModels

import androidx.lifecycle.ViewModel
import com.mostafatamer.chatwithme.network.entity.authenticationDto.RegistrationRequest
import com.mostafatamer.chatwithme.network.repository.UserRepository

class SignUpViewModel(private val userRepository: UserRepository) : ViewModel() {
    fun signUp(
        nickname: String,
        username: String,
        password: String,
        firebaseToken: String,
        function: (success: Boolean) -> Unit,
    ) {
        userRepository.signUp(
            RegistrationRequest(
                username = username,
                password = password,
                nickname = nickname,
                firebaseToken = firebaseToken
            )
        ).setOnSuccess {
            function(it.data != null)
        }.execute()
    }
}



