package io.cleralabs.waito.application.reservation.dto

data class CreateReservationCommand(
    val userId: Long,
    val productId: Long,
)
