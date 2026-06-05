package io.cleralabs.waito.api.config

import io.micrometer.core.instrument.Gauge
import io.micrometer.core.instrument.MeterRegistry
import io.micrometer.core.instrument.binder.MeterBinder
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import java.lang.management.ManagementFactory
import javax.management.MBeanServer
import javax.management.ObjectName

@Configuration
class ApiTomcatMetricsConfig {
    @Bean
    fun apiTomcatMetrics(@Value("\${server.port}") serverPort: Int): MeterBinder {
        return ApiTomcatMetrics(serverPort)
    }
}

private class ApiTomcatMetrics(
    serverPort: Int,
) : MeterBinder {
    private val mBeanServer = ManagementFactory.getPlatformMBeanServer()
    private val connectorName = "http-nio-$serverPort"

    override fun bindTo(registry: MeterRegistry) {
        gauge(registry, "waito.api.tomcat.threads.busy", "currentThreadsBusy")
        gauge(registry, "waito.api.tomcat.threads.current", "currentThreadCount")
        gauge(registry, "waito.api.tomcat.threads.max", "maxThreads")
        gauge(registry, "waito.api.tomcat.connections.current", "connectionCount")
        gauge(registry, "waito.api.tomcat.connections.max", "maxConnections")
    }

    private fun gauge(registry: MeterRegistry, metricName: String, attributeName: String) {
        Gauge.builder(metricName) { attribute(attributeName) }
            .tag("name", connectorName)
            .register(registry)
    }

    private fun attribute(attributeName: String): Double {
        val objectName = findThreadPoolName(mBeanServer, connectorName) ?: return 0.0
        return runCatching {
            (mBeanServer.getAttribute(objectName, attributeName) as Number).toDouble()
        }.getOrDefault(0.0)
    }

    private fun findThreadPoolName(mBeanServer: MBeanServer, connectorName: String): ObjectName? {
        val quotedConnectorName = ObjectName.quote(connectorName)
        return listOf("Tomcat", "Catalina")
            .asSequence()
            .map { ObjectName("$it:type=ThreadPool,name=$quotedConnectorName") }
            .firstOrNull { mBeanServer.isRegistered(it) }
    }
}
