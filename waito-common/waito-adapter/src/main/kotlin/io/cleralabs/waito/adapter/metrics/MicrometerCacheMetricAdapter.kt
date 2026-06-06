package io.cleralabs.waito.adapter.metrics

import io.cleralabs.waito.core.port.CacheMetricPort
import io.micrometer.core.instrument.MeterRegistry
import org.springframework.stereotype.Component

@Component
class MicrometerCacheMetricAdapter(
    private val meterRegistry: MeterRegistry,
) : CacheMetricPort {
    override fun recordHit(cacheName: String) {
        increment("waito.cache.hit", cacheName)
    }

    override fun recordMiss(cacheName: String) {
        increment("waito.cache.miss", cacheName)
    }

    override fun recordPut(cacheName: String) {
        increment("waito.cache.put", cacheName)
    }

    override fun recordStampede(cacheName: String) {
        increment("waito.cache.stampede", cacheName)
    }

    private fun increment(metricName: String, cacheName: String) {
        meterRegistry.counter(
            metricName,
            "cache_name",
            cacheName
        ).increment()
    }
}
