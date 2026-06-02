package io.cleralabs.waito.adapter.kafka

import io.cleralabs.waito.core.port.ReservationRequestPublisher
import org.springframework.beans.factory.annotation.Value
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.stereotype.Component
import java.util.concurrent.TimeUnit

@Component
class KafkaReservationRequestPublisher(
    private val kafkaTemplate: KafkaTemplate<String, String>,
    @Value("\${waito.kafka.topics.reservation-request}")
    private val reservationRequestTopic: String,
) : ReservationRequestPublisher {
    override fun publish(productId: Long, reservationId: Long) {
        kafkaTemplate
            .send(reservationRequestTopic, productId.toString(), reservationId.toString())
            .get(KAFKA_SEND_TIMEOUT_SECONDS, TimeUnit.SECONDS)
    }

    companion object {
        private const val KAFKA_SEND_TIMEOUT_SECONDS = 3L
    }
}
