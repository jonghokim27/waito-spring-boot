package io.cleralabs.waito.api.reservation

import io.cleralabs.waito.application.reservation.ReservationUseCase
import io.cleralabs.waito.application.reservation.dto.CreateReservationCommand
import io.cleralabs.waito.application.reservation.dto.GetReservationQuery
import io.cleralabs.waito.application.reservation.dto.GetReservationsQuery
import io.cleralabs.waito.application.reservation.dto.ReservationResult
import io.cleralabs.waito.core.paging.PagingRequest
import io.cleralabs.waito.core.paging.PagingResult
import io.cleralabs.waito.mvc.annotation.UserId
import io.cleralabs.waito.mvc.dto.ApiResponse
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.ModelAttribute
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api")
class ReservationController(
    private val reservationUseCase: ReservationUseCase,
) {
    @PostMapping("/products/{productId}/reservations")
    fun createReservation(
        @UserId userId: Long,
        @PathVariable productId: Long,
    ): ApiResponse<ReservationResult> {
        val result = reservationUseCase.createReservation(
            CreateReservationCommand(
                userId = userId,
                productId = productId,
            ),
        )

        return ApiResponse.success(result)
    }

    @GetMapping("/reservations")
    fun getReservations(
        @UserId userId: Long,
        @ModelAttribute paging: PagingRequest,
    ): ApiResponse<PagingResult<ReservationResult>> {
        val result = reservationUseCase.getReservations(
            GetReservationsQuery(
                userId = userId,
                paging = paging,
            ),
        )

        return ApiResponse.success(result)
    }

    @GetMapping("/reservations/{reservationId}")
    fun getReservation(
        @UserId userId: Long,
        @PathVariable reservationId: Long,
    ): ApiResponse<ReservationResult> {
        val result = reservationUseCase.getReservation(
            GetReservationQuery(
                userId = userId,
                reservationId = reservationId,
            ),
        )

        return ApiResponse.success(result)
    }
}
