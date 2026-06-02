package io.cleralabs.waito.consumer.reservation

import io.cleralabs.waito.application.reservation.ReservationUseCase
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.stereotype.Component

@Component
class ReservationRequestConsumer(
    private val reservationUseCase: ReservationUseCase,
) {
    @KafkaListener(
        topics = ["\${waito.kafka.topics.reservation-request}"],
        groupId = "\${spring.kafka.consumer.group-id}",
        concurrency = "3",
    )
    fun listen(message: String) {
        val reservationId = message.toLong()
        reservationUseCase.confirmReservation(reservationId)
    }
}
