package com.mostafatamer.chatwithme.presentation.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Modifier

@Composable
fun OutlinedTextFieldFullWidth(text: MutableState<String>) {
    OutlinedTextField(
        value = text.value,
        modifier = Modifier.fillMaxWidth(),
        label = { Text(text = "Username") },
        onValueChange = {
            text.value = it
        }
    )
}