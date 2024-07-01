package com.mostafatamer.chatwithme.utils

import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow

class PaginationState(
    val pageSize: Int,
    var currentPage: Int = 0,
    var hasMorePages: Boolean = true,
) {
    var isLoading by mutableStateOf(false)
}

private interface Paginatable {
    fun isLoading(): Boolean
    fun hasNextPage(): Boolean
    fun loadPages()
    fun incrementPageNumber()
    fun totalItems(): Int
    fun lazyListState(): LazyListState
}

private suspend fun observePagination(paginatable: Paginatable) {
    val state = paginatable.lazyListState()
    snapshotFlow { state.firstVisibleItemIndex }
        .collect {
            val visibleItems = state.layoutInfo.visibleItemsInfo.size
            val firstVisibleItemPosition = state.firstVisibleItemIndex
            val totalItems = paginatable.totalItems()

            if (!paginatable.isLoading() && paginatable.hasNextPage()) {
                if (visibleItems + firstVisibleItemPosition >= totalItems &&
                    firstVisibleItemPosition >= 0
                ) {
                    paginatable.loadPages()
                    paginatable.incrementPageNumber()
                }
            }
        }
}

suspend fun paginationConfiguration(
    paginationState: PaginationState,
    items: List<*>,
    state: LazyListState,
    load: () -> Unit,
) {
    observePagination(
        object : Paginatable {
            override fun isLoading(): Boolean = paginationState.isLoading
            override fun hasNextPage(): Boolean = paginationState.hasMorePages
            override fun totalItems(): Int = items.size
            override fun lazyListState(): LazyListState = state

            override fun loadPages() {
                load.invoke()
            }

            override fun incrementPageNumber() {
                paginationState.currentPage++
                println("loading ${paginationState.currentPage}")
            }
        }
    )
}
