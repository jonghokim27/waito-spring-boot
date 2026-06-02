package io.cleralabs.waito.application.reservation.factory

import io.cleralabs.waito.application.reservation.dto.ReservationResult
import io.cleralabs.waito.domain.product.ProductView
import io.cleralabs.waito.domain.reservation.ReservationView

fun ReservationView.toResult(product: ProductView, waitingCount: Long? = null): ReservationResult {
    return ReservationResult(
        id = id,
        reservationNumber = reservationNumber,
        productId = productId,
        productTitle = product.title,
        quantity = quantity,
        totalPriceAmount = totalPriceAmount,
        currency = currency,
        status = status,
        reservedAt = reservedAt,
        canceledAt = canceledAt,
        waitingCount = waitingCount,
    )
}
