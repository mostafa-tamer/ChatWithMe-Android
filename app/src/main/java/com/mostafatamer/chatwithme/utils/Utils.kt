package com.mostafatamer.chatwithme.utils

import android.content.Context
import android.widget.Toast
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter

fun showToast(context: Context, message: String) {
    Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
}

fun timeMillisConverter(timeMillis: Long): String {
    val dateTime = LocalDateTime.ofInstant(
        Instant.ofEpochSecond(timeMillis), ZoneOffset.UTC
    )

    return dateTime.format(
        DateTimeFormatter.ofPattern("hh:mm a")
    )
}

fun <T> MutableList<T>.addSorted(item: T, comparator: (T) -> Int) {
    val index = binarySearch { comparator(it) }
    val insertIndex = if (index < 0) -(index + 1) else index
    add(insertIndex, item)
}

fun <T> MutableList<T>.addSortedDesc(item: T, comparator: (T) -> Int) {
    val index = binarySearch {
        val result = comparator(it)
        if (result < 0) 1 else -1
    }
    val insertIndex = if (index < 0) -(index + 1) else index
    add(insertIndex, item)
}

