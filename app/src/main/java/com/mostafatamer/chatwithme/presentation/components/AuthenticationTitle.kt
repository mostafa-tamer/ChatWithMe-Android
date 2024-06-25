package com.mostafatamer.chatwithme.presentation.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp

@Composable
fun AuthenticationTitle(text: String) {
    Text(
        text = text,
        textAlign = TextAlign.Center,
        modifier = Modifier
            .fillMaxWidth() ,
        fontSize = 48.sp,
        fontWeight = FontWeight.Bold
    )
}