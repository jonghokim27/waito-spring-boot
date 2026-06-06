package io.cleralabs.waito.core.port

interface CacheMetricPort {
    fun recordHit(cacheName: String)
    fun recordMiss(cacheName: String)
    fun recordPut(cacheName: String)
    fun recordStampede(cacheName: String)
}
