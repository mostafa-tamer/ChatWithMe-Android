package com.mostafatamer.chatwithme.screens

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.mostafatamer.chatwithme.navigation.ScreensRouts
import com.mostafatamer.chatwithme.screens.components.AuthenticationTextField
import com.mostafatamer.chatwithme.screens.components.AuthenticationTitle
import com.mostafatamer.chatwithme.viewModels.LoginViewModel



@Composable
fun LoginScreen(viewModel: LoginViewModel, navController: NavHostController) {
    val context = LocalContext.current

    val username = remember { mutableStateOf("") }
    val password = remember { mutableStateOf("") }

    LaunchedEffect(Unit) {
        viewModel.getFirebaseToken()
    }


    Box(Modifier.padding(16.dp)) {
        Column {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.weight(1f)
            ) {
                AuthenticationTitle("Login")
            }
            Box(
                modifier = Modifier
                    .weight(2f)
                    .fillMaxSize()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                ) {
                    AuthenticationTextField(username, "Username")
                    Spacer(modifier = Modifier.height(16.dp))
                    AuthenticationTextField(password, "Password", true)
                    Spacer(modifier = Modifier.height(32.dp))

                    LoginButton(viewModel, username, password, navController, context)
                    Spacer(modifier = Modifier.height(4.dp))

                    Signup(navController)
                }
            }
        }
    }
}

@Composable
private fun LoginButton(
    viewModel: LoginViewModel,
    username: MutableState<String>,
    password: MutableState<String>,
    navController: NavHostController,
    context: Context,
) {
    Button(
        onClick = {
            val legalToLogin: Boolean = viewModel.firebaseToken != null

            if (legalToLogin) {
                viewModel.login(
                    username.value,
                    password.value,
                ) {
                    if (it) {
                        navController.navigate(ScreensRouts.Main.route) {
                            popUpTo(ScreensRouts.Login.route) {
                                inclusive = true
                            }
                        }
                    } else {
                        Toast.makeText(
                            context,
                            "Login failed",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            } else {
                Toast.makeText(
                    context,
                    "Can not connect, please try again",
                    Toast.LENGTH_SHORT
                ).show()

                viewModel.getFirebaseToken()
            }
        }, Modifier.fillMaxWidth()
    ) {
        Text(text = "Login")
    }
}

@Composable
private fun Signup(navController: NavHostController) {
    Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxWidth()) {
        Text(
            text = "Do not have and account? sign up here",
            color = Color.White,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .clickable {
                    navController.navigate(ScreensRouts.SignUp.route) {
                        popUpTo(ScreensRouts.Login.route) {
                            inclusive = true
                        }
                    }
                }
        )
    }
}


