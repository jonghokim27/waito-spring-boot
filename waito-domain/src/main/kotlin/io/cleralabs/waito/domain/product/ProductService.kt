package io.cleralabs.waito.domain.product

import io.cleralabs.waito.core.enums.ProductCategory
import io.cleralabs.waito.core.enums.ProductStatus
import io.cleralabs.waito.core.exception.BusinessException
import io.cleralabs.waito.domain.product.factory.toView
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service

@Service
class ProductService(
    private val productRepository: ProductRepository,
) {
    fun findOpenProductsByCursor(
        category: ProductCategory?,
        cursor: Long?,
        pageable: Pageable,
    ): List<ProductView> {
        return productRepository.findProductsByCursor(
            status = ProductStatus.OPEN,
            category = category,
            cursor = cursor,
            pageable = pageable,
        ).map { it.toView() }
    }

    fun getProduct(productId: Long): ProductView {
        return productRepository.findById(productId)
            .orElseThrow { BusinessException(PRODUCT_NOT_FOUND_CODE) }
            .toView()
    }

    fun findProduct(productId: Long): ProductView? {
        return productRepository.findById(productId).orElse(null)?.toView()
    }

    fun reserveProduct(productId: Long, quantity: Int) {
        val product = productRepository.findById(productId)
            .orElseThrow { BusinessException(PRODUCT_NOT_FOUND_CODE) }

        product.reserve(quantity)
    }

    companion object {
        private const val PRODUCT_NOT_FOUND_CODE = "product.not-found"
    }
}
