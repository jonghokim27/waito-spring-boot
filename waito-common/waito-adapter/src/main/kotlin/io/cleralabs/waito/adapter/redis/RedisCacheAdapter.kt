package io.cleralabs.waito.adapter.redis

import io.cleralabs.waito.core.port.CachePort
import org.springframework.data.redis.core.StringRedisTemplate
import org.springframework.stereotype.Component
import java.time.Duration

@Component
class RedisCacheAdapter(
    private val redisTemplate: StringRedisTemplate,
) : CachePort {
    override fun get(key: String): String? {
        return redisTemplate.opsForValue().get(key)
    }

    override fun set(key: String, value: String, ttl: Long) {
        redisTemplate.opsForValue().set(key, value, Duration.ofSeconds(ttl))
    }

}
