package io.cleralabs.waito.domain.reservation

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import io.cleralabs.waito.core.enums.ReservationStatus
import java.time.LocalDateTime

@JsonIgnoreProperties(ignoreUnknown = true)
data class ReservationView @JsonCreator constructor(
    @JsonProperty("id") val id: Long,
    @JsonProperty("reservationNumber") val reservationNumber: String,
    @JsonProperty("productId") val productId: Long,
    @JsonProperty("userId") val userId: Long,
    @JsonProperty("quantity") val quantity: Int,
    @JsonProperty("unitPriceAmount") val unitPriceAmount: Long,
    @JsonProperty("currency") val currency: String,
    @JsonProperty("status") val status: ReservationStatus,
    @JsonProperty("reservedAt") val reservedAt: LocalDateTime?,
    @JsonProperty("canceledAt") val canceledAt: LocalDateTime?,
) {
    val totalPriceAmount: Long
        get() = unitPriceAmount * quantity

    val isPending: Boolean
        get() = status == ReservationStatus.PENDING
}
