package io.cleralabs.waito.application.product.dto

import io.cleralabs.waito.core.enums.ProductCategory
import io.cleralabs.waito.core.paging.PagingRequest

data class ProductListQuery(
    val category: ProductCategory? = null,
    val paging: PagingRequest = PagingRequest(),
)
