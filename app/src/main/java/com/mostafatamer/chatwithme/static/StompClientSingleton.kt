package com.mostafatamer.chatwithme.static

import ua.naiksoftware.stomp.Stomp
import ua.naiksoftware.stomp.StompClient


class StompClientSingleton {

    companion object {
        private val headerMap: MutableMap<String, String> = mutableMapOf()

        @Volatile
        private var instance: StompClient? = null

        fun getInstance(token: String? = null): StompClient {
            if (token == null && headerMap["Authorization"] == null) {
                throw IllegalStateException("Token or Authorization header must be provided")
            }

            token?.let { headerMap["Authorization"] = it }

            return instance ?: synchronized(this) {
                instance ?: Stomp.over(
                    Stomp.ConnectionProvider.OKHTTP,
                    "ws://192.168.1.7:9090/gs-guide-websocket",
                    // "wss://chatwithme-sshl.onrender.com/gs-guide-websocket",
                    // "ws://10.1.11.156:9090/gs-guide-websocket",
                    headerMap
                ).also { instance = it }
            }
        }
    }
}