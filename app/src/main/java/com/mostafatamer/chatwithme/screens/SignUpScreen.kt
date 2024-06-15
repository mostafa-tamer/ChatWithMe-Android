package com.mostafatamer.chatwithme.screens

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.mostafatamer.chatwithme.navigation.ScreensRouts
import com.mostafatamer.chatwithme.screens.components.AuthenticationTextField
import com.mostafatamer.chatwithme.screens.components.AuthenticationTitle
import com.mostafatamer.chatwithme.viewModels.SignUpViewModel

@Composable
fun SignUpScreen(
    viewModel: SignUpViewModel,
    navController: NavHostController,
) {

    LaunchedEffect(key1 = Unit) {
        viewModel.getFirebaseToken()
    }

    Box(Modifier.padding(16.dp)) {
        Column(
            modifier = Modifier
        ) {
            Title()
            Content(viewModel,  navController)
        }
    }
}

@Composable
private fun ColumnScope.Content(
    viewModel: SignUpViewModel,
    navController: NavHostController,
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .weight(4f)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(state = rememberScrollState())
        ) {
            AuthenticationTextField(value = viewModel.nickname, title = "Nickname")
            Spacer(modifier = Modifier.height(16.dp))
            AuthenticationTextField(value = viewModel.username, title = "Username")
            Spacer(modifier = Modifier.height(16.dp))
            AuthenticationTextField(
                value = viewModel.password,
                title = "Password",
                isPassword = true
            )
            Spacer(modifier = Modifier.height(16.dp))
            AuthenticationTextField(
                value = viewModel.confirmPassword,
                title = "Confirm Password",
                isPassword = true
            )

            Spacer(modifier = Modifier.height(32.dp))

            Signup(viewModel,  navController)

            Spacer(modifier = Modifier.height(4.dp))

            LoginScreenConfig(navController)
        }
    }
}

@Composable
private fun ColumnScope.Title() {
    Box(
        Modifier.Companion.weight(1f),
        contentAlignment = Alignment.Center
    ) {
        AuthenticationTitle(text = "Signup")
    }
}

@Composable
private fun Signup(
    viewModel: SignUpViewModel,

    navController: NavHostController,
) {
    val context = LocalContext.current

    Button(
        onClick = {
            val fieldsNotEmpty = fieldsNotEmpty(viewModel)

            if (fieldsNotEmpty.all { it }) {
                if (viewModel.password.value == viewModel.confirmPassword.value) {
                    val legalToSignup: Boolean = viewModel.firebaseToken != null
                    if (legalToSignup)
                        signup(viewModel,  navController, context)
                } else {
                    Toast.makeText(
                        context,
                        "Passwords do not match",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            } else {
                Toast.makeText(
                    context,
                    "Please fill all fields",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }, Modifier.fillMaxWidth()
    ) {
        Text(text = "Sign up")
    }
}

private fun signup(
    signUpViewModel: SignUpViewModel,
    navController: NavHostController,
    context: Context,
) {
    signUpViewModel.signUp {
        if (it) {
            Toast.makeText(
                context,
                "Signup Succeeded",
                Toast.LENGTH_SHORT
            ).show()

            navController.navigate(ScreensRouts.Login.route) {
                popUpTo(ScreensRouts.SignUp.route) {
                    inclusive = true
                }
            }
        } else {
            Toast.makeText(
                context,
                "Signup failed",
                Toast.LENGTH_SHORT
            ).show()
        }
    }
}

@Composable
private fun LoginScreenConfig(navController: NavHostController) {
    Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxWidth()) {
        Text(
            text = "already have an account? login here",
            textAlign = TextAlign.Center,
            modifier = Modifier
                .clickable {
                    navController.navigate(ScreensRouts.Login.route) {
                        popUpTo(ScreensRouts.SignUp.route) {
                            inclusive = true
                        }
                    }
                }
        )
    }
}

private fun fieldsNotEmpty(viewModel: SignUpViewModel): List<Boolean> {
    return listOf(
        viewModel.nickname.value.isNotEmpty(),
        viewModel.username.value.isNotEmpty(),
        viewModel.password.value.isNotEmpty(),
        viewModel.confirmPassword.value.isNotEmpty()
    )
}

