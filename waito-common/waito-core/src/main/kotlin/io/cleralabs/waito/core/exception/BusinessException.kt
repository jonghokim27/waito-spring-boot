package io.cleralabs.waito.core.exception

class BusinessException(
    val code: String,
    val args: Array<Any> = emptyArray()
) : RuntimeException(code)