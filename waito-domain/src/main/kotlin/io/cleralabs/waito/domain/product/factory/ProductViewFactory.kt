package io.cleralabs.waito.domain.product.factory

import io.cleralabs.waito.domain.product.Product
import io.cleralabs.waito.domain.product.ProductView

fun Product.toView(): ProductView {
    return ProductView(
        id = id,
        title = title,
        description = description,
        category = category,
        status = status,
        venueName = venueName,
        venueAddress = venueAddress,
        performanceStartAt = performanceStartAt,
        performanceEndAt = performanceEndAt,
        reservationOpenAt = reservationOpenAt,
        reservationCloseAt = reservationCloseAt,
        priceAmount = priceAmount,
        currency = currency,
        totalQuantity = totalQuantity,
        reservedQuantity = reservedQuantity,
        minReservationQuantity = minReservationQuantity,
        maxReservationQuantity = maxReservationQuantity,
        runningMinutes = runningMinutes,
        ageRating = ageRating,
        thumbnailUrl = thumbnailUrl,
    )
}
