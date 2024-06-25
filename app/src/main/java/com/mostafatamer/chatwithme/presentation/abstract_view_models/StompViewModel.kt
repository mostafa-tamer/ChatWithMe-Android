package com.mostafatamer.chatwithme.presentation.abstract_view_models

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModel
import com.mostafatamer.chatwithme.services.StompService

abstract class StompViewModel(private val stompService: StompService) : ViewModel(),
    LifecycleEventObserver {
    override fun onStateChanged(source: LifecycleOwner, event: Lifecycle.Event) {
        when (event) {
            Lifecycle.Event.ON_RESUME -> connect()

            else -> {}
        }
    }

    private fun connect() {
        if (!stompService.isStompConnected()) {
            stompService.connect()
        }
    }

    fun disconnect() {
        if (stompService.isStompConnected()) {
            stompService.disconnect()
        }
    }
}