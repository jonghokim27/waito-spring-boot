package io.cleralabs.waito.core.validation

import io.cleralabs.waito.core.exception.BusinessException
import io.cleralabs.waito.core.exception.InvalidInputException

fun requireInput(value: Boolean, code: String, args: Array<Any> = emptyArray()) {
    if (!value) {
        throw InvalidInputException(code, args)
    }
}

fun requireBusiness(value: Boolean, code: String, args: Array<Any> = emptyArray()) {
    if (!value) {
        throw BusinessException(code, args)
    }
}

fun requireSystem(value: Boolean, message: String) {
    if (!value) {
        throw RuntimeException(message)
    }
}
