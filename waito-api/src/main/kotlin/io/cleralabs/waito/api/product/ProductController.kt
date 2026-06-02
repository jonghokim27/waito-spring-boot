package io.cleralabs.waito.api.product

import io.cleralabs.waito.application.product.ProductQueryUseCase
import io.cleralabs.waito.application.product.dto.ProductDetailResult
import io.cleralabs.waito.application.product.dto.ProductListQuery
import io.cleralabs.waito.application.product.dto.ProductSummaryResult
import io.cleralabs.waito.core.enums.ProductCategory
import io.cleralabs.waito.core.paging.PagingRequest
import io.cleralabs.waito.core.paging.PagingResult
import io.cleralabs.waito.mvc.annotation.UserId
import io.cleralabs.waito.mvc.dto.ApiResponse
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.ModelAttribute
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/products")
class ProductController(
    private val productQueryUseCase: ProductQueryUseCase,
) {
    @GetMapping
    fun getProducts(
        @RequestParam(required = false) category: ProductCategory?,
        @ModelAttribute paging: PagingRequest,
    ): ApiResponse<PagingResult<ProductSummaryResult>> {
        val result = productQueryUseCase.getProducts(
            ProductListQuery(
                category = category,
                paging = paging,
            ),
        )

        return ApiResponse.success(result)
    }

    @GetMapping("/{productId}")
    fun getProduct(
        @UserId userId: Long,
        @PathVariable productId: Long,
    ): ApiResponse<ProductDetailResult> {
        return ApiResponse.success(productQueryUseCase.getProduct(productId, userId))
    }

}
