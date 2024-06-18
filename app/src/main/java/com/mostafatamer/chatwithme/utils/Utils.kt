package com.mostafatamer.chatwithme.utils

import ua.naiksoftware.stomp.Stomp
import ua.naiksoftware.stomp.StompClient
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter

fun timeMillisConverter(timeMillis: Long): String {
    val dateTime = LocalDateTime.ofInstant(
        Instant.ofEpochSecond(timeMillis), ZoneOffset.UTC
    )

    return dateTime.format(
        DateTimeFormatter.ofPattern("hh:mm a")
    )
}

fun createStompClient(token: String): StompClient {
    return Stomp.over(
        Stomp.ConnectionProvider.OKHTTP,
        "ws://192.168.1.7:9090/gs-guide-websocket",
        // "wss://chatwithme-sshl.onrender.com/gs-guide-websocket",
        // "ws://10.1.11.156:9090/gs-guide-websocket",
        mutableMapOf("Authorization" to token)
    )
}