package io.cleralabs.waito.application.reservation.dto

data class GetReservationQuery(
    val userId: Long,
    val reservationId: Long,
)
