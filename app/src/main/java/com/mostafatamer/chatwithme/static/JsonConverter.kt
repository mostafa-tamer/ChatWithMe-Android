package com.mostafatamer.chatwithme.static

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.JsonPrimitive
import com.google.gson.JsonSerializer
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class JsonConverter {

    companion object {
        private var gson = GsonBuilder()
            .registerTypeAdapter(
                LocalDateTime::class.java, JsonSerializer<LocalDateTime> { src, _, _ ->
                    JsonPrimitive(
                        src.format(
                            DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSX")
                        )
                    )
                }
            ).create()

        fun getInstance(): Gson = gson
    }
}