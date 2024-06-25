package com.mostafatamer.chatwithme.presentation.components

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp

@Composable
fun AuthenticationTextField(
    value: MutableState<String>,
    title: String,
    isPassword: Boolean = false,
) {
    Text(text = title, fontWeight = FontWeight.Bold )
    Spacer(modifier = Modifier.height(8.dp))
    OutlinedTextField(
        singleLine = true,
        value = value.value,
        onValueChange = { value.value = it },
        modifier = Modifier.fillMaxWidth(),
        visualTransformation = if (isPassword) PasswordVisualTransformation() else VisualTransformation.None
    )
}