package io.cleralabs.waito.application.product.dto

import io.cleralabs.waito.core.enums.ProductCategory
import io.cleralabs.waito.core.enums.ProductStatus
import java.time.LocalDateTime

data class ProductDetailResult(
    val id: Long,
    val title: String,
    val description: String,
    val category: ProductCategory,
    val status: ProductStatus,
    val venueName: String,
    val venueAddress: String,
    val performanceStartAt: LocalDateTime,
    val performanceEndAt: LocalDateTime,
    val reservationOpenAt: LocalDateTime,
    val reservationCloseAt: LocalDateTime,
    val priceAmount: Long,
    val currency: String,
    val totalQuantity: Int,
    val reservedQuantity: Int,
    val availableQuantity: Int,
    val minReservationQuantity: Int,
    val maxReservationQuantity: Int,
    val runningMinutes: Int,
    val ageRating: String,
    val thumbnailUrl: String?,
    val reservable: Boolean,
    val alreadyReserved: Boolean,
)
