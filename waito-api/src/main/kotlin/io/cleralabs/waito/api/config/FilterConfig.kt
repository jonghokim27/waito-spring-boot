package io.cleralabs.waito.api.config

import io.cleralabs.waito.mvc.filter.JwtAuthFilter
import io.cleralabs.waito.mvc.filter.LoggingFilter
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.web.servlet.FilterRegistrationBean
import org.springframework.context.MessageSource
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.Ordered
import tools.jackson.databind.ObjectMapper

@Configuration
class FilterConfig(
    @Value("\${waito.jwt.private-key}")
    private val jwtPrivateKey: String,
    private val objectMapper: ObjectMapper,
    private val messageSource: MessageSource,
) {

    @Bean
    fun loggingFilterRegistration(): FilterRegistrationBean<LoggingFilter> {
        return FilterRegistrationBean(LoggingFilter(objectMapper, messageSource)).apply {
            addUrlPatterns("/*")
            order = Ordered.HIGHEST_PRECEDENCE
        }
    }

    @Bean
    fun jwtAuthFilterRegistration(): FilterRegistrationBean<JwtAuthFilter> {
        return FilterRegistrationBean(JwtAuthFilter(jwtPrivateKey, objectMapper, messageSource)).apply {
            addUrlPatterns("/*")
            order = Ordered.HIGHEST_PRECEDENCE + 1
        }
    }

}
