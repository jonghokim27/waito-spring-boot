package io.cleralabs.waito.application.reservation.dto

import io.cleralabs.waito.core.enums.ReservationStatus
import java.time.LocalDateTime

data class ReservationResult(
    val id: Long?,
    val reservationNumber: String?,
    val productId: Long,
    val productTitle: String,
    val quantity: Int,
    val totalPriceAmount: Long,
    val currency: String,
    val status: ReservationStatus,
    val reservedAt: LocalDateTime?,
    val canceledAt: LocalDateTime?,
    val waitingCount: Long?,
)
