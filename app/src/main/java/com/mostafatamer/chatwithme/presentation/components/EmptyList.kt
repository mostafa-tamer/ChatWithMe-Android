package com.mostafatamer.chatwithme.presentation.components

import androidx.compose.foundation.layout.BoxScope
import androidx.compose.material.Text
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.sp

@Composable
fun BoxScope.EmptyList(text: String) {
    Text(
        text = text,
        modifier = Modifier
            .align(Alignment.Center),
        color = MaterialTheme.colorScheme.onSurface,
        fontSize = 20.sp
    )
}