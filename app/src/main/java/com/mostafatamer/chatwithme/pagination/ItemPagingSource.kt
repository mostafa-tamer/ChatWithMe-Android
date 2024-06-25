package com.mostafatamer.chatwithme.pagination

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.mostafatamer.chatwithme.network.entity.ApiResponse
import com.mostafatamer.chatwithme.network.entity.Page
import com.mostafatamer.chatwithme.utils.CallDecorator
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class ItemPagingSource<T : Any>(
    private val apiCall: (pageNumber: Int, size: Int) -> CallDecorator<ApiResponse<Page<T>>>,
) : PagingSource<Int, T>() {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, T> {
        return try {
            suspendCoroutine { continuation ->
                val nextPage = params.key ?: 0
                apiCall.invoke(nextPage, params.loadSize)
                    .setOnSuccess { apiResponse ->
                        apiResponse?.data?.let {
                            continuation.resume(
                                LoadResult.Page(
                                    data = it.content,
                                    prevKey = if (nextPage == 1) null else nextPage - 1,
                                    nextKey = if (it.empty) null else nextPage + 1
                                )
                            )
                        }
                    }.execute()
            }
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }

    override fun getRefreshKey(state: PagingState<Int, T>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            state.closestPageToPosition(anchorPosition)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(anchorPosition)?.nextKey?.minus(1)
        }
    }
}
