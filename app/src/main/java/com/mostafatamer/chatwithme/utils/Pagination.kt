package com.mostafatamer.chatwithme.utils

import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.snapshotFlow


interface Pagination {
    fun isLoading(): Boolean
    fun hasNextPage(): Boolean
    fun loadPages()
    fun incrementPageNumber()
    fun totalItems(): Int
    fun lazyListState(): LazyListState

    companion object {
        suspend fun pagination(pagination: Pagination) {
            val state = pagination.lazyListState()
            snapshotFlow { state.firstVisibleItemIndex }
                .collect {
                    val visibleItems = state.layoutInfo.visibleItemsInfo.size
                    val firstVisibleItemPosition = state.firstVisibleItemIndex
                    val totalItems = pagination.totalItems()

                    if (!pagination.isLoading() && pagination.hasNextPage()) {
                        if (visibleItems + firstVisibleItemPosition >= totalItems &&
                            firstVisibleItemPosition >= 0
                        ) {
                            pagination.loadPages()
                            pagination.incrementPageNumber()
                        }
                    }
                }
        }
    }
}