package io.cleralabs.waito.mvc.filter

import io.cleralabs.waito.mvc.dto.ApiResponse
import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import mu.KotlinLogging
import org.slf4j.MDC
import org.springframework.context.MessageSource
import org.springframework.http.MediaType
import org.springframework.web.filter.OncePerRequestFilter
import org.springframework.web.util.ContentCachingRequestWrapper
import org.springframework.web.util.ContentCachingResponseWrapper
import tools.jackson.databind.ObjectMapper
import java.nio.charset.StandardCharsets
import java.util.UUID

class LoggingFilter(
    private val objectMapper: ObjectMapper,
    private val messageSource: MessageSource,
) : OncePerRequestFilter() {

    private val log = KotlinLogging.logger {}

    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        val requestId = UUID.randomUUID().toString()

        MDC.put("requestId", requestId)

        val wrappedRequest = ContentCachingRequestWrapper(request, 0)
        val wrappedResponse = ContentCachingResponseWrapper(response)

        val start = System.currentTimeMillis()

        try {
            filterChain.doFilter(wrappedRequest, wrappedResponse)
        } catch (e: Exception) {
            log.error(e.message, e)

            if (wrappedResponse.isCommitted) {
                throw e
            }

            writeErrorResponse(wrappedRequest, wrappedResponse)
        } finally {
            val elapsed = System.currentTimeMillis() - start

            val requestBody = if (isLoggableContentType(request.contentType)) {
                getRequestBody(wrappedRequest)
            } else {
                null
            }

            val responseBody = if (isLoggableContentType(wrappedResponse.contentType)) {
                getResponseBody(wrappedResponse)
            } else {
                null
            }

            log.info {
                """
                [${request.method}] [${getRequestTarget(request)}]
                requestBody=$requestBody
                responseStatus=${wrappedResponse.status}
                responseBody=$responseBody
                elapsedMs=$elapsed
                """.trimIndent()
            }

            wrappedResponse.copyBodyToResponse()

            MDC.clear()
        }
    }

    private fun getRequestTarget(request: HttpServletRequest): String {
        val queryString = request.queryString

        if (queryString.isNullOrBlank()) {
            return request.requestURI
        }

        return "${request.requestURI}?$queryString"
    }

    private fun getRequestBody(request: ContentCachingRequestWrapper): String {
        val buf = request.contentAsByteArray
        if (buf.isEmpty()) return ""

        return String(buf, 0, buf.size, StandardCharsets.UTF_8)
    }

    private fun getResponseBody(response: ContentCachingResponseWrapper): String {
        val buf = response.contentAsByteArray
        if (buf.isEmpty()) return ""

        return String(buf, 0, buf.size, StandardCharsets.UTF_8)
    }

    private fun isLoggableContentType(contentType: String?): Boolean {
        if (contentType == null) {
            return false
        }

        return LOGGABLE_CONTENT_TYPES.any { s -> contentType.contains(s) }
    }

    private fun writeErrorResponse(request: HttpServletRequest, response: HttpServletResponse) {
        val message = messageSource.getMessage(
            INTERNAL_SERVER_ERROR_CODE,
            null,
            INTERNAL_SERVER_ERROR_CODE,
            request.locale,
        ) ?: INTERNAL_SERVER_ERROR_CODE

        response.reset()
        response.status = HttpServletResponse.SC_INTERNAL_SERVER_ERROR
        response.contentType = MediaType.APPLICATION_JSON_VALUE
        response.characterEncoding = Charsets.UTF_8.name()
        objectMapper.writeValue(
            response.writer,
            ApiResponse.error(
                code = INTERNAL_SERVER_ERROR_CODE,
                message = message,
            ),
        )
    }

    companion object {
        private val LOGGABLE_CONTENT_TYPES = listOf("application/json")
        private const val INTERNAL_SERVER_ERROR_CODE = "common.internal-server-error"
    }
}
