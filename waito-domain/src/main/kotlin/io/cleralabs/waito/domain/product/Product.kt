package io.cleralabs.waito.domain.product

import io.cleralabs.waito.core.enums.ProductCategory
import io.cleralabs.waito.core.enums.ProductStatus
import io.cleralabs.waito.core.validation.requireBusiness
import io.cleralabs.waito.core.validation.requireInput
import io.cleralabs.waito.domain.common.BaseEntity
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.Index
import jakarta.persistence.Table
import java.time.LocalDateTime

@Entity
@Table(
	name = "products",
	indexes = [
		Index(name = "idx_products_status_reservation_open_at", columnList = "status,reservation_open_at"),
		Index(name = "idx_products_category_performance_start_at", columnList = "category,performance_start_at"),
	],
)
class Product(
	@Column(name = "title", nullable = false, length = 120)
	var title: String,

	@Column(name = "description", nullable = false, columnDefinition = "TEXT")
	var description: String,

	@Enumerated(EnumType.STRING)
	@Column(name = "category", nullable = false, length = 30)
	var category: ProductCategory,

	@Enumerated(EnumType.STRING)
	@Column(name = "status", nullable = false, length = 30)
	var status: ProductStatus = ProductStatus.DRAFT,

	@Column(name = "venue_name", nullable = false, length = 100)
	var venueName: String,

	@Column(name = "venue_address", nullable = false, length = 255)
	var venueAddress: String,

	@Column(name = "performance_start_at", nullable = false)
	var performanceStartAt: LocalDateTime,

	@Column(name = "performance_end_at", nullable = false)
	var performanceEndAt: LocalDateTime,

	@Column(name = "reservation_open_at", nullable = false)
	var reservationOpenAt: LocalDateTime,

	@Column(name = "reservation_close_at", nullable = false)
	var reservationCloseAt: LocalDateTime,

	@Column(name = "price_amount", nullable = false)
	var priceAmount: Long,

	@Column(name = "currency", nullable = false, length = 3)
	var currency: String = "KRW",

	@Column(name = "total_quantity", nullable = false)
	var totalQuantity: Int,

	@Column(name = "reserved_quantity", nullable = false)
	var reservedQuantity: Int = 0,

	@Column(name = "min_reservation_quantity", nullable = false)
	var minReservationQuantity: Int = 1,

	@Column(name = "max_reservation_quantity", nullable = false)
	var maxReservationQuantity: Int = 4,

	@Column(name = "running_minutes", nullable = false)
	var runningMinutes: Int,

	@Column(name = "age_rating", nullable = false, length = 30)
	var ageRating: String,

	@Column(name = "thumbnail_url", length = 500)
	var thumbnailUrl: String? = null,
) : BaseEntity() {
	init {
		validateSchedule()
		validateQuantities()
		requireInput(priceAmount >= 0, "product.price-amount-invalid")
		requireInput(runningMinutes > 0, "product.running-minutes-invalid")
	}

	val availableQuantity: Int
		get() = totalQuantity - reservedQuantity

	fun open() {
		requireBusiness(status == ProductStatus.DRAFT || status == ProductStatus.CLOSED, "product.open-not-allowed")

		status = ProductStatus.OPEN
	}

	fun close() {
		requireBusiness(status == ProductStatus.OPEN, "product.close-not-allowed")

		status = ProductStatus.CLOSED
	}

	fun sellOut() {
		status = ProductStatus.SOLD_OUT
	}

	fun cancel() {
		requireBusiness(reservedQuantity == 0, "product.cancel-not-allowed")

		status = ProductStatus.CANCELED
	}

	fun reserve(quantity: Int, requestedAt: LocalDateTime = LocalDateTime.now()) {
		requireReservable(quantity, requestedAt)

		reservedQuantity += quantity

		if (availableQuantity == 0) {
			sellOut()
		}
	}

	fun release(quantity: Int) {
		requireInput(quantity > 0, "reservation.quantity-invalid")
		requireBusiness(reservedQuantity >= quantity, "product.release-not-allowed")

		reservedQuantity -= quantity

		if (status == ProductStatus.SOLD_OUT && availableQuantity > 0) {
			status = ProductStatus.OPEN
		}
	}

	fun isReservable(quantity: Int, requestedAt: LocalDateTime = LocalDateTime.now()): Boolean {
		return status == ProductStatus.OPEN &&
			!requestedAt.isBefore(reservationOpenAt) &&
			requestedAt.isBefore(reservationCloseAt) &&
			quantity in minReservationQuantity..maxReservationQuantity &&
			availableQuantity >= quantity
	}

	private fun requireReservable(quantity: Int, requestedAt: LocalDateTime) {
		requireBusiness(status == ProductStatus.OPEN, "product.not-open")
		requireBusiness(!requestedAt.isBefore(reservationOpenAt), "reservation.not-opened")
		requireBusiness(requestedAt.isBefore(reservationCloseAt), "reservation.closed")
		requireInput(
			quantity in minReservationQuantity..maxReservationQuantity,
			"reservation.quantity-out-of-range",
			arrayOf(minReservationQuantity, maxReservationQuantity),
		)
		requireBusiness(availableQuantity >= quantity, "reservation.not-enough-quantity")
	}

	private fun validateSchedule() {
		requireInput(performanceStartAt.isBefore(performanceEndAt), "product.performance-period-invalid")
		requireInput(reservationOpenAt.isBefore(reservationCloseAt), "product.reservation-period-invalid")
		requireInput(
			reservationCloseAt.isBefore(performanceStartAt) || reservationCloseAt == performanceStartAt,
			"product.reservation-close-at-invalid",
		)
	}

	private fun validateQuantities() {
		requireInput(totalQuantity > 0, "product.total-quantity-invalid")
		requireInput(reservedQuantity >= 0, "product.reserved-quantity-invalid")
		requireInput(reservedQuantity <= totalQuantity, "product.reserved-quantity-exceeded")
		requireInput(minReservationQuantity > 0, "product.min-reservation-quantity-invalid")
		requireInput(maxReservationQuantity >= minReservationQuantity, "product.max-reservation-quantity-invalid")
	}
}
