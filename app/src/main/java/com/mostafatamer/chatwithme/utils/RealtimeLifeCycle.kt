package com.mostafatamer.chatwithme.utils

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.platform.LocalLifecycleOwner
import com.mostafatamer.chatwithme.presentation.abstract_view_models.StompViewModel

@Composable
fun RealtimeLifeCycle(stompService: StompViewModel) {
    val lifecycleOwner = rememberUpdatedState(LocalLifecycleOwner.current)

    DisposableEffect(Unit) {
        val lifecycle = lifecycleOwner.value.lifecycle

        lifecycle.addObserver(stompService)

        onDispose {
            lifecycle.removeObserver(stompService)
            stompService.disconnect()
        }
    }
}