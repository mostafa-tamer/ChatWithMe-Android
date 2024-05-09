package com.mostafatamer.chatwithme.static

import ua.naiksoftware.stomp.Stomp
import ua.naiksoftware.stomp.StompClient


class StompClientSingleton {

    companion object {
        private val headerMap: MutableMap<String, String> = mutableMapOf()

        fun createInstance(token: String? = null): StompClient {
            token?.let { headerMap["Authorization"] = it }
            return Stomp.over(
                Stomp.ConnectionProvider.OKHTTP,
                "ws://192.168.1.14:9090/gs-guide-websocket",
//                "ws://10.1.11.156:9090/gs-guide-websocket",
                headerMap,
            )
        }
    }
}