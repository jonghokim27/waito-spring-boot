package io.cleralabs.waito.adapter.kafka

import org.apache.kafka.clients.admin.NewTopic
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class KafkaTopicConfig {
    @Bean
    fun reservationRequestTopic(
        @Value("\${waito.kafka.topics.reservation-request}") topicName: String,
    ): NewTopic {
        return NewTopic(topicName, RESERVATION_REQUEST_PARTITIONS, REPLICATION_FACTOR)
    }

    companion object {
        private const val RESERVATION_REQUEST_PARTITIONS = 24
        private const val REPLICATION_FACTOR: Short = 3
    }
}
