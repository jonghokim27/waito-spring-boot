package io.cleralabs.waito.core.exception

class InvalidInputException(
    val code: String,
    val args: Array<Any> = emptyArray()
) : RuntimeException(code)