package io.cleralabs.waito.api.config

import io.cleralabs.waito.mvc.dto.ApiResponse
import io.cleralabs.waito.core.exception.BusinessException
import io.cleralabs.waito.core.exception.InvalidInputException
import mu.KotlinLogging
import org.springframework.context.MessageSource
import org.springframework.http.HttpStatus
import org.springframework.http.converter.HttpMessageNotReadableException
import org.springframework.web.HttpRequestMethodNotSupportedException
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.MissingServletRequestParameterException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestControllerAdvice
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException
import org.springframework.web.servlet.NoHandlerFoundException
import org.springframework.web.servlet.resource.NoResourceFoundException
import java.util.Locale

@RestControllerAdvice
class GlobalControllerAdvice(
    private val messageSource: MessageSource
) {
    val log = KotlinLogging.logger {}

    companion object {
        const val BAD_REQUEST_CODE = "common.bad-request"
        const val NOT_FOUND_CODE = "common.not-found"
        const val INTERNAL_SERVER_ERROR_CODE = "common.internal-server-error"
    }

    // =========================
    // 400 Bad Request
    // =========================

    @ExceptionHandler(MethodArgumentNotValidException::class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    fun handleValidation(e: MethodArgumentNotValidException): ApiResponse<Nothing?> {
        return handleError(BAD_REQUEST_CODE)
    }

    @ExceptionHandler(HttpMessageNotReadableException::class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    fun handleUnreadable(e: HttpMessageNotReadableException): ApiResponse<Nothing?> {
        return handleError(BAD_REQUEST_CODE)
    }

    @ExceptionHandler(MissingServletRequestParameterException::class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    fun handleMissingParam(e: MissingServletRequestParameterException): ApiResponse<Nothing?> {
        return handleError(BAD_REQUEST_CODE)
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException::class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    fun handleTypeMismatch(e: MethodArgumentTypeMismatchException): ApiResponse<Nothing?> {
        return handleError(BAD_REQUEST_CODE)
    }

    @ExceptionHandler(InvalidInputException::class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    fun handleTypeMismatch(e: InvalidInputException): ApiResponse<Nothing?> {
        return handleError(e.code, e.args)
    }

    // =========================
    // 404 Not Found
    // =========================

    @ExceptionHandler(NoResourceFoundException::class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    fun handleNotFound(e: NoResourceFoundException): ApiResponse<Nothing?> {
        return handleError(NOT_FOUND_CODE)
    }

    @ExceptionHandler(NoHandlerFoundException::class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    fun handleNotFound(e: NoHandlerFoundException): ApiResponse<Nothing?> {
        return handleError(NOT_FOUND_CODE)
    }

    // =========================
    // 405 Method Not Allowed
    // =========================

    @ExceptionHandler(HttpRequestMethodNotSupportedException::class)
    @ResponseStatus(HttpStatus.METHOD_NOT_ALLOWED)
    fun handleMethodNotAllowed(e: HttpRequestMethodNotSupportedException): ApiResponse<Nothing?> {
        return handleError(BAD_REQUEST_CODE)
    }

    // =========================
    // 422 Unprocessable Content
    // =========================

    @ExceptionHandler(BusinessException::class)
    @ResponseStatus(HttpStatus.UNPROCESSABLE_CONTENT)
    fun handleApiException(e: BusinessException): ApiResponse<Nothing?> {
        return handleError(e.code, e.args)
    }

    // =========================
    // 500 Internal Server Error
    // =========================

    @ExceptionHandler(Exception::class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    fun handleException(e: Exception): ApiResponse<Nothing?> {
        log.error(e.message, e)

        return handleError(INTERNAL_SERVER_ERROR_CODE)
    }

    private fun handleError(code: String, args: Array<Any> = emptyArray()): ApiResponse<Nothing?> {
        val message = messageSource.getMessage(
            code,
            args,
            code,
            Locale.getDefault()
        )

        return ApiResponse.error(code, message ?: code)
    }

}
