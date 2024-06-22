package com.mostafatamer.chatwithme.network.entity

data class Page<T>(
    val content: List<T>,
    val pageable: Pageable,
    val total: Int,
    val last: Boolean,
    val totalPages: Int,
    val totalElements: Int,
    val size: Int,
    val number: Int,
    val sort: Sort,
    val first: Boolean,
    val numberOfElements: Int,
    val empty: Boolean
)

data class Pageable(
    val pageNumber: Int,
    val pageSize: Int,
    val sort: Sort,
    val offset: Int,
    val unpaged: Boolean,
    val paged: Boolean
)

data class Sort(
    val orders: List<Any>, // Change to actual type if known
    val empty: Boolean,
    val sorted: Boolean,
    val unsorted: Boolean
)
