package com.mostafatamer.chatwithme.screens.abstract_view_models

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.google.firebase.messaging.FirebaseMessaging

abstract class AuthenticationViewModel : ViewModel() {
      var firebaseToken by mutableStateOf<String?>(null)
        private set

    fun getFirebaseToken() {
        FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
            firebaseToken = task.result
        }
    }
}