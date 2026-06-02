package io.cleralabs.waito.core.port

interface ReservationRequestPublisher {
    fun publish(productId: Long, reservationId: Long)
}
