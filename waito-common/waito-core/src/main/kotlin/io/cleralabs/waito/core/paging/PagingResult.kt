package io.cleralabs.waito.core.paging

data class PagingResult<T>(
    val items: List<T>,
    val nextCursor: Long?,
    val hasNext: Boolean,
)
