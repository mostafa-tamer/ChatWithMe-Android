package com.mostafatamer.chatwithme.utils

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

fun <T> MutableList<T>.addSorted(item: T, comparator: (t: T) -> Int) {
    val index = binarySearch { comparator.invoke(it) }
    val insertIndex = if (index < 0) -(index + 1) else index
    add(insertIndex, item)
}

