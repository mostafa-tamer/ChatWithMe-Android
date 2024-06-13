package com.mostafatamer.chatwithme.viewModels

import androidx.compose.runtime.mutableStateOf
import com.mostafatamer.chatwithme.network.entity.authenticationDto.RegistrationRequest
import com.mostafatamer.chatwithme.network.repository.UserRepository

class SignUpViewModel(private val userRepository: UserRepository) : AuthenticationViewModel() {
    val username = mutableStateOf("")
    val nickname = mutableStateOf("")
    val password = mutableStateOf("")
    val confirmPassword = mutableStateOf("")

    fun signUp(function: (success: Boolean) -> Unit) {
        userRepository.signUp(
            RegistrationRequest(
                username = username.value,
                password = password.value,
                nickname = nickname.value,
                firebaseToken = firebaseToken!!
            )
        ).setOnSuccess {
            function(it.data != null)
        }.execute()
    }
}



