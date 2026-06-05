package io.cleralabs.waito.domain.config

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.jdbc.autoconfigure.DataSourceProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Primary
import org.springframework.jdbc.datasource.LazyConnectionDataSourceProxy
import javax.sql.DataSource

@Configuration
class DataSourceConfig {
    @Bean
    @ConfigurationProperties("spring.datasource.hikari")
    fun actualDataSource(properties: DataSourceProperties): DataSource {
        return properties.initializeDataSourceBuilder().build()
    }

    @Bean
    @Primary
    fun dataSource(actualDataSource: DataSource): DataSource {
        return LazyConnectionDataSourceProxy(actualDataSource)
    }
}
