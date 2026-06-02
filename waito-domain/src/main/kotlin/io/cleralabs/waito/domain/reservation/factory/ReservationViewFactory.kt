package io.cleralabs.waito.domain.reservation.factory

import io.cleralabs.waito.domain.reservation.Reservation
import io.cleralabs.waito.domain.reservation.ReservationView

fun Reservation.toView(): ReservationView {
    return ReservationView(
        id = id,
        reservationNumber = reservationNumber,
        productId = productId,
        userId = userId,
        quantity = quantity,
        unitPriceAmount = unitPriceAmount,
        currency = currency,
        status = status,
        reservedAt = reservedAt,
        canceledAt = canceledAt,
    )
}
