package io.cleralabs.waito.application.product

import io.cleralabs.waito.application.product.dto.ProductDetailResult
import io.cleralabs.waito.application.product.dto.ProductListQuery
import io.cleralabs.waito.application.product.dto.ProductSummaryResult
import io.cleralabs.waito.application.product.factory.toDetailResult
import io.cleralabs.waito.application.product.factory.toSummaryResult
import io.cleralabs.waito.core.paging.PagingResult
import io.cleralabs.waito.domain.product.ProductService
import io.cleralabs.waito.domain.reservation.ReservationService
import org.springframework.data.domain.PageRequest
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class ProductQueryUseCase(
    private val productService: ProductService,
    private val reservationService: ReservationService,
) {
    @Transactional(readOnly = true)
    fun getProducts(query: ProductListQuery): PagingResult<ProductSummaryResult> {
        val size = query.paging.normalizedSize()
        val products = productService.findOpenProductsByCursor(
            category = query.category,
            cursor = query.paging.cursor,
            pageable = PageRequest.of(0, size + 1),
        )
        val items = products.take(size)
        val lastItem = items.lastOrNull()

        return PagingResult(
            items = items.map { it.toSummaryResult() },
            nextCursor = if (products.size > size && lastItem != null) {
                lastItem.id
            } else {
                null
            },
            hasNext = products.size > size,
        )
    }

    @Transactional(readOnly = true)
    fun getProduct(productId: Long, userId: Long): ProductDetailResult {
        val product = productService.getProduct(productId)
        val alreadyReserved = reservationService.hasActiveReservation(
            productId = product.id,
            userId = userId,
        )

        return product.toDetailResult(alreadyReserved)
    }
}
