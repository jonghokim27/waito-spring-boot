package io.cleralabs.waito.domain.reservation

import io.cleralabs.waito.core.enums.ReservationStatus
import io.cleralabs.waito.core.validation.requireInput
import io.cleralabs.waito.domain.common.BaseEntity
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.Index
import jakarta.persistence.Table
import java.time.LocalDateTime
import java.util.UUID

@Entity
@Table(
	name = "reservations",
	indexes = [
		Index(name = "uk_reservations_reservation_number", columnList = "reservation_number", unique = true),
		Index(name = "idx_reservations_product_id_status", columnList = "product_id,status"),
		Index(name = "idx_reservations_user_id_reserved_at", columnList = "user_id,reserved_at"),
	],
)
class Reservation(
	@Column(name = "product_id", nullable = false)
	var productId: Long,

	@Column(name = "user_id", nullable = false)
	var userId: Long,

	@Column(name = "quantity", nullable = false)
	var quantity: Int,

	@Column(name = "unit_price_amount", nullable = false)
	var unitPriceAmount: Long,

	@Column(name = "currency", nullable = false, length = 3)
	var currency: String,

	@Column(name = "reservation_number", nullable = false, length = 40, unique = true)
	var reservationNumber: String = generateReservationNumber(),

	@Enumerated(EnumType.STRING)
	@Column(name = "status", nullable = false, length = 30)
	var status: ReservationStatus = ReservationStatus.PENDING,

	@Column(name = "reserved_at")
	var reservedAt: LocalDateTime? = null,

	@Column(name = "canceled_at")
	var canceledAt: LocalDateTime? = null,
) : BaseEntity() {

	init {
		requireInput(quantity > 0, "reservation.quantity-invalid")
		requireInput(unitPriceAmount >= 0, "reservation.unit-price-amount-invalid")
		requireInput(productId > 0, "reservation.product-id-invalid")
		requireInput(userId > 0, "reservation.user-id-invalid")
	}

	val totalPriceAmount: Long
		get() = unitPriceAmount * quantity

	fun confirm(reservedAt: LocalDateTime = LocalDateTime.now()) {
		status = ReservationStatus.CONFIRMED
		this.reservedAt = reservedAt
	}

	fun fail() {
		status = ReservationStatus.FAILED
	}

	fun cancel(canceledAt: LocalDateTime = LocalDateTime.now()) {
		status = ReservationStatus.CANCELED
		this.canceledAt = canceledAt
	}

	companion object {
		fun createPending(productId: Long, userId: Long, unitPriceAmount: Long, currency: String): Reservation {
			return Reservation(
				productId = productId,
				userId = userId,
				quantity = DEFAULT_QUANTITY,
				unitPriceAmount = unitPriceAmount,
				currency = currency,
			)
		}

		private fun generateReservationNumber(): String {
			return UUID.randomUUID().toString().replace("-", "").take(20).uppercase()
		}

		private const val DEFAULT_QUANTITY = 1
	}
}
