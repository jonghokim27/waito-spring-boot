package io.cleralabs.waito.mvc.resolver

import io.cleralabs.waito.mvc.annotation.UserId
import jakarta.servlet.http.HttpServletRequest
import org.springframework.core.MethodParameter
import org.springframework.web.bind.support.WebDataBinderFactory
import org.springframework.web.context.request.NativeWebRequest
import org.springframework.web.method.support.HandlerMethodArgumentResolver
import org.springframework.web.method.support.ModelAndViewContainer

class UserIdArgumentResolver : HandlerMethodArgumentResolver {
    override fun supportsParameter(parameter: MethodParameter): Boolean {
        return parameter.hasParameterAnnotation(UserId::class.java) &&
            (
                parameter.parameterType == Long::class.javaPrimitiveType ||
                    parameter.parameterType == Long::class.javaObjectType
                )
    }

    override fun resolveArgument(
        parameter: MethodParameter,
        mavContainer: ModelAndViewContainer?,
        webRequest: NativeWebRequest,
        binderFactory: WebDataBinderFactory?,
    ): Any {
        val request = webRequest.getNativeRequest(HttpServletRequest::class.java)
            ?: throw IllegalStateException("HttpServletRequest is required.")

        return request.getAttribute(USER_ID_ATTRIBUTE) as? Long
            ?: throw IllegalStateException("Authenticated userId is required.")
    }

    companion object {
        private const val USER_ID_ATTRIBUTE = "userId"
    }
}
