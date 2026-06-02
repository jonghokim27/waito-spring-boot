package io.cleralabs.waito.application.reservation

import io.cleralabs.waito.application.reservation.dto.CreateReservationCommand
import io.cleralabs.waito.application.reservation.dto.GetReservationQuery
import io.cleralabs.waito.application.reservation.dto.GetReservationsQuery
import io.cleralabs.waito.application.reservation.dto.ReservationResult
import io.cleralabs.waito.application.reservation.factory.toResult
import io.cleralabs.waito.core.exception.BusinessException
import io.cleralabs.waito.core.paging.PagingResult
import io.cleralabs.waito.core.port.ReservationRequestPublisher
import io.cleralabs.waito.core.transaction.afterCommit
import io.cleralabs.waito.domain.product.ProductService
import io.cleralabs.waito.domain.reservation.ReservationService
import mu.KotlinLogging
import org.springframework.data.domain.PageRequest
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class ReservationUseCase(
    private val productService: ProductService,
    private val reservationService: ReservationService,
    private val reservationRequestPublisher: ReservationRequestPublisher,
) {
    private val log = KotlinLogging.logger {}

    @Transactional
    fun createReservation(command: CreateReservationCommand): ReservationResult {
        val product = productService.getProduct(command.productId)
        val reservation = reservationService.createPendingReservation(
            productId = product.id,
            userId = command.userId,
            unitPriceAmount = product.priceAmount,
            currency = product.currency,
        )
        val reservationId = reservation.id

        afterCommit {
            reservationRequestPublisher.publish(
                productId = command.productId,
                reservationId = reservationId,
            )
        }

        return reservation.toResult(
            product = product,
            waitingCount = reservationService.waitingCountOf(reservation),
        )
    }

    @Transactional(readOnly = true)
    fun getReservations(query: GetReservationsQuery): PagingResult<ReservationResult> {
        val size = query.paging.normalizedSize()
        val reservations = reservationService.findReservationsByUserIdAndCursor(
            userId = query.userId,
            cursor = query.paging.cursor,
            pageable = PageRequest.of(0, size + 1),
        )
        val items = reservations.take(size)

        return PagingResult(
            items = items.map { reservation ->
                reservation.toResult(
                    product = productService.getProduct(reservation.productId),
                    waitingCount = reservationService.waitingCountOf(reservation),
                )
            },
            nextCursor = if (reservations.size > size) items.lastOrNull()?.id else null,
            hasNext = reservations.size > size,
        )
    }

    @Transactional(readOnly = true)
    fun getReservation(query: GetReservationQuery): ReservationResult {
        val reservation = reservationService.getReservation(query.reservationId)
        if (reservation.userId != query.userId) {
            throw BusinessException(RESERVATION_NOT_FOUND_CODE)
        }

        val product = productService.getProduct(reservation.productId)

        return reservation.toResult(
            product = product,
            waitingCount = if (reservation.isPending) reservationService.waitingCountOf(reservation) else null,
        )
    }

    @Transactional
    fun confirmReservation(reservationId: Long) {
        val reservation = reservationService.getReservation(reservationId)

        if (reservationService.hasConfirmedReservation(
                productId = reservation.productId,
                userId = reservation.userId,
            )
        ) {
            reservationService.failReservation(reservationId)
            return
        }

        try {
            productService.reserveProduct(
                productId = reservation.productId,
                quantity = reservation.quantity,
            )
        } catch (exception: BusinessException) {
            reservationService.failReservation(reservationId)
            return
        }

        reservationService.confirmReservation(reservationId)
    }

    companion object {
        private const val RESERVATION_NOT_FOUND_CODE = "reservation.not-found"
    }
}
