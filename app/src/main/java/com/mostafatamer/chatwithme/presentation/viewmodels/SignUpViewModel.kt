package com.mostafatamer.chatwithme.presentation.viewmodels

import androidx.compose.runtime.mutableStateOf
import com.mostafatamer.chatwithme.domain.model.dto.authenticationDto.RegistrationRequest
import com.mostafatamer.chatwithme.data.repository.restful.AuthenticationRepository
import com.mostafatamer.chatwithme.presentation.viewmodels.abstract_view_models.AuthenticationViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class SignUpViewModel @Inject constructor(
    private val authenticationRepository: AuthenticationRepository,
) : AuthenticationViewModel() {
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



