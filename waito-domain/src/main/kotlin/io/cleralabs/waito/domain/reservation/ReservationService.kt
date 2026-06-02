package io.cleralabs.waito.domain.reservation

import io.cleralabs.waito.core.enums.ReservationStatus
import io.cleralabs.waito.core.exception.BusinessException
import io.cleralabs.waito.domain.reservation.factory.toView
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service

@Service
class ReservationService(
    private val reservationRepository: ReservationRepository
) {
    fun createPendingReservation(
        productId: Long,
        userId: Long,
        unitPriceAmount: Long,
        currency: String,
    ): ReservationView {
        val reservation = reservationRepository.save(
            Reservation.createPending(
                productId = productId,
                userId = userId,
                unitPriceAmount = unitPriceAmount,
                currency = currency,
            ),
        )

        return reservation.toView()
    }

    fun getReservation(reservationId: Long): ReservationView {
        return reservationRepository.findById(reservationId)
            .orElseThrow { BusinessException(RESERVATION_NOT_FOUND_CODE) }
            .toView()
    }

    fun findReservationsByUserIdAndCursor(
        userId: Long,
        cursor: Long?,
        pageable: Pageable,
    ): List<ReservationView> {
        return reservationRepository.findReservationsByUserIdAndCursor(
            userId = userId,
            cursor = cursor,
            pageable = pageable,
        ).map { it.toView() }
    }

    fun hasActiveReservation(productId: Long, userId: Long): Boolean {
        val userIds = reservationRepository.findUserIdsByProductIdAndStatusIn(
            productId = productId,
            statuses = ACTIVE_RESERVATION_STATUSES,
        )

        return userIds.contains(userId)
    }

    fun hasConfirmedReservation(productId: Long, userId: Long): Boolean {
        val userIds = reservationRepository.findUserIdsByProductIdAndStatus(
            productId = productId,
            status = ReservationStatus.CONFIRMED,
        )

        return userIds.contains(userId)
    }

    fun waitingCountOf(reservation: ReservationView): Long? {
        if (reservation.status != ReservationStatus.PENDING) {
            return null
        }

        return reservationRepository.countByProductIdAndStatusAndIdLessThan(
            productId = reservation.productId,
            status = ReservationStatus.PENDING,
            id = reservation.id,
        )
    }

    fun confirmReservation(reservationId: Long): ReservationView {
        val reservation = reservationRepository.findById(reservationId)
            .orElseThrow { RuntimeException("Reservation must exist. reservationId=$reservationId") }

        if (reservation.status != ReservationStatus.PENDING) {
            throw BusinessException(RESERVATION_CONFIRM_NOT_ALLOWED_CODE, arrayOf(reservation.id))
        }

        reservation.confirm()

        return reservation.toView()
    }

    fun failReservation(reservationId: Long): ReservationView {
        val reservation = reservationRepository.findById(reservationId)
            .orElseThrow { RuntimeException("Reservation must exist. reservationId=$reservationId") }

        if (reservation.status != ReservationStatus.PENDING) {
            throw BusinessException(RESERVATION_FAIL_NOT_ALLOWED_CODE, arrayOf(reservation.id))
        }

        reservation.fail()

        return reservation.toView()
    }

    companion object {
        private const val RESERVATION_NOT_FOUND_CODE = "reservation.not-found"
        private const val RESERVATION_CONFIRM_NOT_ALLOWED_CODE = "reservation.confirm-not-allowed"
        private const val RESERVATION_FAIL_NOT_ALLOWED_CODE = "reservation.fail-not-allowed"
        private val ACTIVE_RESERVATION_STATUSES = listOf(
            ReservationStatus.PENDING,
            ReservationStatus.CONFIRMED,
        )
    }
}
