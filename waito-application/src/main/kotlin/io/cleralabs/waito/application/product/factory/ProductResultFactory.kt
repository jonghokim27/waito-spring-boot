package io.cleralabs.waito.application.product.factory

import io.cleralabs.waito.application.product.dto.ProductDetailResult
import io.cleralabs.waito.application.product.dto.ProductSummaryResult
import io.cleralabs.waito.domain.product.ProductView

fun ProductView.toSummaryResult(): ProductSummaryResult {
    return ProductSummaryResult(
        id = id,
        title = title,
        category = category,
        status = status,
        venueName = venueName,
        performanceStartAt = performanceStartAt,
        reservationOpenAt = reservationOpenAt,
        reservationCloseAt = reservationCloseAt,
        priceAmount = priceAmount,
        currency = currency,
        availableQuantity = availableQuantity,
        thumbnailUrl = thumbnailUrl,
    )
}

fun ProductView.toDetailResult(alreadyReserved: Boolean): ProductDetailResult {
    return ProductDetailResult(
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
        availableQuantity = availableQuantity,
        minReservationQuantity = minReservationQuantity,
        maxReservationQuantity = maxReservationQuantity,
        runningMinutes = runningMinutes,
        ageRating = ageRating,
        thumbnailUrl = thumbnailUrl,
        reservable = isReservable(minReservationQuantity),
        alreadyReserved = alreadyReserved,
    )
}
