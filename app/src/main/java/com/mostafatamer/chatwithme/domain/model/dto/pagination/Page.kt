package com.mostafatamer.chatwithme.domain.model.dto.pagination

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


