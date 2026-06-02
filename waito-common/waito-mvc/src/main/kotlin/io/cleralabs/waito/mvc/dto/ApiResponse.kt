package io.cleralabs.waito.mvc.dto

data class ApiResponse<T>(
    val result: T?,
    val error: ApiError? = null
) {
    companion object {
        fun <S> success(result: S): ApiResponse<S> {
            return ApiResponse(result)
        }

        fun error(code: String, message: String): ApiResponse<Nothing?> {
            return ApiResponse(null, ApiError(code, message))
        }
    }

    data class ApiError(
        val code: String,
        val message: String
    )
}