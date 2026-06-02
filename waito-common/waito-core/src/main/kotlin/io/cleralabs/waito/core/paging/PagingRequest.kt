package io.cleralabs.waito.core.paging

data class PagingRequest(
    val cursor: Long? = null,
    val size: Int = DEFAULT_SIZE,
) {
    fun normalizedSize(): Int {
        return size.coerceIn(MIN_SIZE, MAX_SIZE)
    }

    companion object {
        const val DEFAULT_SIZE = 20
        private const val MIN_SIZE = 1
        private const val MAX_SIZE = 100
    }
}
