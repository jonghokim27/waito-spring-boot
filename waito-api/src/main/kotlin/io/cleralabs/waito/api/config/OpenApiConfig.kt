package io.cleralabs.waito.api.config

import io.cleralabs.waito.mvc.annotation.UserId
import io.swagger.v3.oas.models.Components
import io.swagger.v3.oas.models.OpenAPI
import io.swagger.v3.oas.models.info.Info
import io.swagger.v3.oas.models.security.SecurityRequirement
import io.swagger.v3.oas.models.security.SecurityScheme
import org.springdoc.core.customizers.OperationCustomizer
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class OpenApiConfig {
    @Bean
    fun openApi(): OpenAPI {
        return OpenAPI()
            .info(
                Info()
                    .title("Waito API")
                    .version("v1"),
            )
            .components(
                Components()
                    .addSecuritySchemes(
                        AUTHORIZATION_SCHEME_NAME,
                        SecurityScheme()
                            .type(SecurityScheme.Type.HTTP)
                            .scheme("bearer")
                            .bearerFormat("JWT"),
                    ),
            )
            .addSecurityItem(SecurityRequirement().addList(AUTHORIZATION_SCHEME_NAME))
    }

    @Bean
    fun userIdParameterCustomizer(): OperationCustomizer {
        return OperationCustomizer { operation, handlerMethod ->
            val userIdParameterNames = handlerMethod.methodParameters
                .filter { it.hasParameterAnnotation(UserId::class.java) }
                .mapNotNull { it.parameterName }
                .toSet()

            if (userIdParameterNames.isNotEmpty()) {
                operation.parameters = operation.parameters
                    ?.filterNot { it.name in userIdParameterNames }
                    ?.toMutableList()
            }

            operation
        }
    }

    companion object {
        private const val AUTHORIZATION_SCHEME_NAME = "Authorization"
    }
}
