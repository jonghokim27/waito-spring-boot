package io.cleralabs.waito.application.product.dto

import io.cleralabs.waito.core.enums.ProductCategory
import io.cleralabs.waito.core.enums.ProductStatus
import java.time.LocalDateTime

data class ProductSummaryResult(
    val id: Long,
    val title: String,
    val category: ProductCategory,
    val status: ProductStatus,
    val venueName: String,
    val performanceStartAt: LocalDateTime,
    val reservationOpenAt: LocalDateTime,
    val reservationCloseAt: LocalDateTime,
    val priceAmount: Long,
    val currency: String,
    val availableQuantity: Int,
    val thumbnailUrl: String?,
)
