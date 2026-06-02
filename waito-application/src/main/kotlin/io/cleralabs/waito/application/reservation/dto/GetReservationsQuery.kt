package io.cleralabs.waito.application.reservation.dto

import io.cleralabs.waito.core.paging.PagingRequest

data class GetReservationsQuery(
    val userId: Long,
    val paging: PagingRequest = PagingRequest(),
)
