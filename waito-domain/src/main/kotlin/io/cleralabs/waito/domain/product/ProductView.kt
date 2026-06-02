package io.cleralabs.waito.domain.product

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import io.cleralabs.waito.core.enums.ProductCategory
import io.cleralabs.waito.core.enums.ProductStatus
import java.time.LocalDateTime

@JsonIgnoreProperties(ignoreUnknown = true)
data class ProductView @JsonCreator constructor(
    @JsonProperty("id") val id: Long,
    @JsonProperty("title") val title: String,
    @JsonProperty("description") val description: String,
    @JsonProperty("category") val category: ProductCategory,
    @JsonProperty("status") val status: ProductStatus,
    @JsonProperty("venueName") val venueName: String,
    @JsonProperty("venueAddress") val venueAddress: String,
    @JsonProperty("performanceStartAt") val performanceStartAt: LocalDateTime,
    @JsonProperty("performanceEndAt") val performanceEndAt: LocalDateTime,
    @JsonProperty("reservationOpenAt") val reservationOpenAt: LocalDateTime,
    @JsonProperty("reservationCloseAt") val reservationCloseAt: LocalDateTime,
    @JsonProperty("priceAmount") val priceAmount: Long,
    @JsonProperty("currency") val currency: String,
    @JsonProperty("totalQuantity") val totalQuantity: Int,
    @JsonProperty("reservedQuantity") val reservedQuantity: Int,
    @JsonProperty("minReservationQuantity") val minReservationQuantity: Int,
    @JsonProperty("maxReservationQuantity") val maxReservationQuantity: Int,
    @JsonProperty("runningMinutes") val runningMinutes: Int,
    @JsonProperty("ageRating") val ageRating: String,
    @JsonProperty("thumbnailUrl") val thumbnailUrl: String?,
) {
    val availableQuantity: Int
        get() = totalQuantity - reservedQuantity

    fun isReservable(quantity: Int, requestedAt: LocalDateTime = LocalDateTime.now()): Boolean {
        return status == ProductStatus.OPEN &&
            !requestedAt.isBefore(reservationOpenAt) &&
            requestedAt.isBefore(reservationCloseAt) &&
            quantity in minReservationQuantity..maxReservationQuantity &&
            availableQuantity >= quantity
    }
}
