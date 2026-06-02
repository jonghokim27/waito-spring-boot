package io.cleralabs.waito.mvc.filter

import com.auth0.jwt.JWT
import com.auth0.jwt.JWTVerifier
import com.auth0.jwt.algorithms.Algorithm
import com.auth0.jwt.exceptions.JWTVerificationException
import io.cleralabs.waito.mvc.dto.ApiResponse
import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.context.MessageSource
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.web.filter.OncePerRequestFilter
import tools.jackson.databind.ObjectMapper

class JwtAuthFilter(
	privateKey: String,
	private val objectMapper: ObjectMapper,
	private val messageSource: MessageSource,
) : OncePerRequestFilter() {
	private val verifier: JWTVerifier = JWT.require(Algorithm.HMAC256(privateKey)).build()

	override fun shouldNotFilter(request: HttpServletRequest): Boolean {
		val requestUri = request.requestURI

		return SWAGGER_PATH_PREFIXES.any { requestUri.startsWith(it) } ||
			requestUri == SWAGGER_UI_HTML_PATH
	}

	override fun doFilterInternal(
		request: HttpServletRequest,
		response: HttpServletResponse,
		filterChain: FilterChain,
	) {
		val token = resolveBearerToken(request)

		if (token == null) {
			writeUnauthorizedResponse(request, response)
			return
		}

		try {
			val jwt = verifier.verify(token)
			val userId = jwt.getClaim(USER_ID_CLAIM).asLong()
				?: throw JWTVerificationException("JWT userId claim is required.")

			request.setAttribute(USER_ID_ATTRIBUTE, userId)
			filterChain.doFilter(request, response)
		} catch (e: JWTVerificationException) {
			logger.error(e.message, e)
			writeUnauthorizedResponse(request, response)
		}
	}

	private fun writeUnauthorizedResponse(request: HttpServletRequest, response: HttpServletResponse) {
		val message = messageSource.getMessage(
			UNAUTHORIZED_CODE,
			null,
			UNAUTHORIZED_CODE,
			request.locale,
		) ?: UNAUTHORIZED_CODE

		response.status = HttpServletResponse.SC_UNAUTHORIZED
		response.contentType = MediaType.APPLICATION_JSON_VALUE
		response.characterEncoding = Charsets.UTF_8.name()
		objectMapper.writeValue(
			response.writer,
			ApiResponse.error(
				code = UNAUTHORIZED_CODE,
				message = message,
			),
		)
	}

	private fun resolveBearerToken(request: HttpServletRequest): String? {
		val authorization = request.getHeader(HttpHeaders.AUTHORIZATION) ?: return null

		if (!authorization.startsWith(BEARER_PREFIX, ignoreCase = true)) {
			return null
		}

		return authorization.substring(BEARER_PREFIX.length).trim().takeIf { it.isNotBlank() }
	}

	companion object {
		private const val USER_ID_CLAIM = "userId"
		private const val BEARER_PREFIX = "Bearer "
		private const val USER_ID_ATTRIBUTE = "userId"
		private const val UNAUTHORIZED_CODE = "auth.unauthorized"
		private const val SWAGGER_UI_HTML_PATH = "/swagger-ui.html"
		private val SWAGGER_PATH_PREFIXES = listOf(
			"/swagger-ui/",
			"/v3/api-docs",
			"/actuator/",
		)
	}
}
