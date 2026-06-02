package io.cleralabs.waito.batch

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.persistence.autoconfigure.EntityScan
import org.springframework.boot.runApplication
import org.springframework.data.jpa.repository.config.EnableJpaRepositories
import org.springframework.scheduling.annotation.EnableAsync
import org.springframework.scheduling.annotation.EnableScheduling

@SpringBootApplication(scanBasePackages = ["io.cleralabs.waito"])
@EntityScan(basePackages = ["io.cleralabs.waito.domain"])
@EnableJpaRepositories(basePackages = ["io.cleralabs.waito.domain"])
@EnableAsync
@EnableScheduling
class BatchApplication

fun main(args: Array<String>) {
	runApplication<BatchApplication>(*args)
}
