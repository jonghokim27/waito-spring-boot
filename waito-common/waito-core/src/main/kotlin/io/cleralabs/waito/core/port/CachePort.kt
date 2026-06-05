package io.cleralabs.waito.core.port

interface CachePort {
    fun get(key: String): String?
    fun set(key: String, value: String, ttl: Long)
}