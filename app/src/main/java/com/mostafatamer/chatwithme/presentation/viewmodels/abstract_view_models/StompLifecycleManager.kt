package com.mostafatamer.chatwithme.presentation.viewmodels.abstract_view_models

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import com.mostafatamer.chatwithme.data.services.StompService

class StompLifecycleManager(private vararg val stompServices: StompService) :
    LifecycleEventObserver {
    override fun onStateChanged(source: LifecycleOwner, event: Lifecycle.Event) {
        when (event) {
            Lifecycle.Event.ON_RESUME -> connect()
            Lifecycle.Event.ON_STOP -> disconnect()
            else -> {}
        }
    }

    private fun connect() {
        stompServices.forEach { stompService ->
            if (!stompService.isStompConnected()) {
                stompService.connect()
            }
        }
    }

    private fun disconnect() {
        stompServices.forEach { stompService ->
            if (stompService.isStompConnected()) {
                stompService.disconnect()
            }
        }
    }
}