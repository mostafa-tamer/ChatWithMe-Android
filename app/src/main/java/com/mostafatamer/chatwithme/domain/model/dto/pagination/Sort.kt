package com.mostafatamer.chatwithme.domain.model.dto.pagination

data class Sort(
    val orders: List<Any>, // Change to actual type if known
    val empty: Boolean,
    val sorted: Boolean,
    val unsorted: Boolean
)