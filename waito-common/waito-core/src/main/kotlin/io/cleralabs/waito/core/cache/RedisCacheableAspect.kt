package io.cleralabs.waito.core.cache

import io.cleralabs.waito.core.port.CacheMetricPort
import io.cleralabs.waito.core.port.CachePort
import org.aspectj.lang.ProceedingJoinPoint
import org.aspectj.lang.annotation.Around
import org.aspectj.lang.annotation.Aspect
import org.aspectj.lang.reflect.MethodSignature
import org.springframework.context.expression.MethodBasedEvaluationContext
import org.springframework.core.DefaultParameterNameDiscoverer
import org.springframework.expression.spel.standard.SpelExpressionParser
import org.springframework.stereotype.Component
import tools.jackson.databind.ObjectMapper
import java.lang.reflect.Method
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.atomic.AtomicInteger

@Aspect
@Component
class RedisCacheableAspect(
    private val cachePort: CachePort,
    private val cacheMetricPort: CacheMetricPort,
    private val objectMapper: ObjectMapper
) {
    private val parser = SpelExpressionParser()
    private val parameterNameDiscoverer = DefaultParameterNameDiscoverer()
    private val activeLoads = ConcurrentHashMap<String, AtomicInteger>()

    @Around("@annotation(redisCacheable)")
    fun around(joinPoint: ProceedingJoinPoint, redisCacheable: RedisCacheable): Any? {
        val method = (joinPoint.signature as MethodSignature).method
        val cacheKey = evaluateKey(redisCacheable.cacheName, redisCacheable.key, method, joinPoint.args)

        cachePort.get(cacheKey)?.let {
            cacheMetricPort.recordHit(redisCacheable.cacheName)
            return objectMapper.readValue(it, objectMapper.constructType(method.genericReturnType))
        }

        cacheMetricPort.recordMiss(redisCacheable.cacheName)

        val activeLoadCount = activeLoads.computeIfAbsent(cacheKey) { AtomicInteger() }.incrementAndGet()
        if (activeLoadCount > 1) {
            cacheMetricPort.recordStampede(redisCacheable.cacheName)
        }

        try {
            val result = joinPoint.proceed()
            if (result != null || !redisCacheable.unlessNull) {
                cachePort.set(cacheKey, objectMapper.writeValueAsString(result), redisCacheable.ttlSeconds)
                cacheMetricPort.recordPut(redisCacheable.cacheName)
            }
            return result
        } finally {
            activeLoads.computeIfPresent(cacheKey) { _, activeLoadsForKey ->
                activeLoadsForKey.takeIf { it.decrementAndGet() > 0 }
            }
        }
    }

    private fun evaluateKey(
        cacheName: String,
        key: String,
        method: Method,
        args: Array<Any?>,
    ): String {
        val context = MethodBasedEvaluationContext(
            null,
            method,
            args,
            parameterNameDiscoverer,
        )

        val key = parser.parseExpression(key)
            .getValue(context, String::class.java) ?: throw RuntimeException("Invalid cache key expression.")

        return "$CACHE_PREFIX$cacheName:$key"
    }

    companion object {
        private const val CACHE_PREFIX = "cache:"
    }
}
