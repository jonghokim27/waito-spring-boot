package io.cleralabs.waito.domain.product

import io.cleralabs.waito.core.enums.ProductCategory
import io.cleralabs.waito.core.enums.ProductStatus
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query

interface ProductRepository : JpaRepository<Product, Long> {
    @Query(
        """
        select p
        from Product p
        where p.status = :status
          and (:category is null or p.category = :category)
          and (:cursor is null or p.id > :cursor)
        order by p.id asc
        """,
    )
    fun findProductsByCursor(
        status: ProductStatus,
        category: ProductCategory?,
        cursor: Long?,
        pageable: Pageable,
    ): List<Product>
}
