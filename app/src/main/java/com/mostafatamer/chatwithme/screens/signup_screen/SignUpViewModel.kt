package com.mostafatamer.chatwithme.screens.signup_screen

import androidx.compose.runtime.mutableStateOf
import com.mostafatamer.chatwithme.network.entity.authenticationDto.RegistrationRequest
import com.mostafatamer.chatwithme.network.repository.AuthenticationRepository
import com.mostafatamer.chatwithme.screens.abstract_view_models.AuthenticationViewModel

class SignUpViewModel(private val authenticationRepository: AuthenticationRepository) : AuthenticationViewModel() {
    val username = mutableStateOf("")
    val nickname = mutableStateOf("")
    val password = mutableStateOf("")
    val confirmPassword = mutableStateOf("")

    fun signUp(function: (success: Boolean) -> Unit) {
        authenticationRepository.signUp(
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



