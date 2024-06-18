package com.mostafatamer.chatwithme.utils

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.JsonPrimitive
import com.google.gson.JsonSerializer
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
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

const val WS_URI = "ws://192.168.1.7:9090/gs-guide-websocket"
// "wss://chatwithme-sshl.onrender.com/gs-guide-websocket",
// "ws://10.1.11.156:9090/gs-guide-websocket",

fun getStompClient(token: String): StompClient {
    return Stomp.over(
        Stomp.ConnectionProvider.OKHTTP,
        WS_URI, mutableMapOf("Authorization" to token)
    )
}

const val BASE_URL = "http://192.168.1.7:9090"
//                        "https://chatwithme-sshl.onrender.com"
//                        "http://10.1.11.156:9090"

fun getRetrofit(token: String? = null): Retrofit {
    val okHttpClient = token?.let {
        OkHttpClient.Builder()
            .addInterceptor { chain ->
                val request = chain.request().newBuilder()
                    .addHeader("Authorization", it)
                    .build()
                chain.proceed(request)
            }.build()
    }

    return Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(
            GsonConverterFactory.create(JsonConverter)
        ).client(
            okHttpClient ?: OkHttpClient.Builder()
                .build()
        ).build()
}

fun getRetrofit(): Retrofit {
    return Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(
            GsonConverterFactory.create(JsonConverter)
        ).build()
}

val JsonConverter: Gson = GsonBuilder()
    .registerTypeAdapter(
        LocalDateTime::class.java, JsonSerializer<LocalDateTime> { src, _, _ ->
            JsonPrimitive(
                src.format(
                    DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSX")
                )
            )
        }
    ).create()