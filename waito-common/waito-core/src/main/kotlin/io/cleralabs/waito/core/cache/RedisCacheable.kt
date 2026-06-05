package io.cleralabs.waito.core.cache

@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
annotation class RedisCacheable(
    val cacheName: String,
    val key: String = "",
    val ttlSeconds: Long = DEFAULT_TTL_SECONDS,
    val unlessNull: Boolean = true,
)

private const val DEFAULT_TTL_SECONDS = 86_400L
