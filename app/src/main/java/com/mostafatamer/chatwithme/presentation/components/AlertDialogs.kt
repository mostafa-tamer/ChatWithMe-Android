package com.mostafatamer.chatwithme.presentation.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun SendToUserAlertDialog(
    openDialog: MutableState<Boolean>,
    onOkButton: (String, String) -> Unit,
) {

    val username = remember { mutableStateOf("") }
    val message = remember { mutableStateOf("") }

    if (openDialog.value) {
        AlertDialog(
            onDismissRequest = { },

            title = { Text(text = "Send Friend Request") },
            text = {
                Body(username, message)
            },
            confirmButton = {
                Button(
                    onClick = {
                        onOkButton(username.value, message.value)
                    }
                ) {
                    Text("Send")
                }
            },
            dismissButton = {
                Button(
                    onClick = { openDialog.value = false }
                ) {
                    Text("Cancel")
                }
            }
        )
    } else {
        username.value = ""
        message.value = ""
    }
}

@Composable
private fun Body(
    username: MutableState<String>,
    message: MutableState<String>,
) {

    Column {
        OutlinedTextField(
            value = username.value,
            modifier = Modifier.fillMaxWidth(),
            label = {
                Text(text = "Username")
            },
            onValueChange = {
                username.value = it
            }
        )
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(
            modifier = Modifier.fillMaxWidth(),
            value = message.value,
            minLines = 3, maxLines = 3,
            label = {
                Text(text = "Message")
            },
            onValueChange = {
                message.value = it
            }
        )
    }
}


@Composable
fun DefaultAlertDialog(
    showAlertDialog: MutableState<Boolean>,
    title: String,
    message: String,
    okButtonText: String = "Ok",
    onOkButton: () -> Unit,
) {

    if (showAlertDialog.value) {
        AlertDialog(
            onDismissRequest = { },
            title = { Text(text = title) },
            text = {
                Text(text = message)
            },
            confirmButton = {
                Button(
                    onClick = {
                        onOkButton()
                    }
                ) {
                    Text(okButtonText)
                }
            },
            dismissButton = {
                Button(
                    onClick = { showAlertDialog.value = false }
                ) {
                    Text("Cancel")
                }
            }
        )
    }
}