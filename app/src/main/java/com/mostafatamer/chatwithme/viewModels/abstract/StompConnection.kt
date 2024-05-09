package com.mostafatamer.chatwithme.viewModels.abstract

import com.mostafatamer.chatwithme.services.StompService

interface StompConnection {

    fun ensureStompConnected()

    fun ensureStompConnected(stompService: StompService) {
        if (!stompService.isStompConnected()) {
            stompService.connect()
        }
    }

    fun cleanUp()

    fun cleanUp(stompService: StompService) {
        stompService.disconnect()
    }



}