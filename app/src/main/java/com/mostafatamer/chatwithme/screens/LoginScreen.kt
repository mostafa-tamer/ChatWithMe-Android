package com.mostafatamer.chatwithme.screens

import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.google.firebase.messaging.FirebaseMessaging
import com.mostafatamer.chatwithme.navigation.Screen
import com.mostafatamer.chatwithme.viewModels.LoginViewModel

@Composable
fun LoginScreen(viewModel: LoginViewModel, navController: NavHostController) {
    val context = LocalContext.current

    var username by remember { mutableStateOf("mostafa") }
    var password by remember { mutableStateOf("12345678") }
    var firebaseToken by remember { mutableStateOf("") }

    LaunchedEffect(key1 = Unit) {
        FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
            if (!task.isSuccessful)
                throw RuntimeException("Failed to get Firebase token ${task.exception?.message}")

            val token = task.result
            firebaseToken = token
            println(firebaseToken)
        }
    }

    Column(verticalArrangement = Arrangement.Center, modifier = Modifier
        .fillMaxSize()
        .padding(16.dp)) {
        TextField(
            placeholder = { Text(text = "Username") },
            value = username,
            onValueChange = { username = it },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(16.dp))
        TextField(
            placeholder = { Text(text = "Password") },
            value = password,
            onValueChange = { password = it },
            modifier = Modifier.fillMaxWidth(),
            visualTransformation = PasswordVisualTransformation(),
        )
        Spacer(modifier = Modifier.height(32.dp))

        Button(
            onClick = {
                if (firebaseToken.isEmpty()) {
                    Toast.makeText(
                        context,
                        "Check the network connection",
                        Toast.LENGTH_SHORT
                    ).show()
                    return@Button
                }

                viewModel.login(username, password, firebaseToken) {
                    if (it) {
                        navController.navigate(Screen.Main.route)
                    } else {
                        Toast.makeText(
                            context,
                            "Login failed",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }, Modifier.fillMaxWidth()
        ) {
            Text(text = "Login")
        }
        Text(
            text = "Do not have and account? sign up here",
            color = Color.White,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .fillMaxWidth()
                .clickable {
                    navController.navigate(Screen.SignUp.route)
                }
        )
    }
}