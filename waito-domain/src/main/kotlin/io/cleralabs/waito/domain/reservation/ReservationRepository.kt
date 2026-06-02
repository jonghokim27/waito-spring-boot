package io.cleralabs.waito.domain.reservation

import io.cleralabs.waito.core.enums.ReservationStatus
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query

interface ReservationRepository : JpaRepository<Reservation, Long> {
    @Query(
        """
        select distinct r.userId
        from Reservation r
        where r.productId = :productId
          and r.status in :statuses
        """,
    )
    fun findUserIdsByProductIdAndStatusIn(productId: Long, statuses: Collection<ReservationStatus>): List<Long>

    @Query(
        """
        select distinct r.userId
        from Reservation r
        where r.productId = :productId
          and r.status = :status
        """,
    )
    fun findUserIdsByProductIdAndStatus(productId: Long, status: ReservationStatus): List<Long>

    @Query(
        """
        select r
        from Reservation r
        where r.userId = :userId
          and (:cursor is null or r.id < :cursor)
        order by r.id desc
        """,
    )
    fun findReservationsByUserIdAndCursor(userId: Long, cursor: Long?, pageable: Pageable): List<Reservation>

    fun countByProductIdAndStatusAndIdLessThan(productId: Long, status: ReservationStatus, id: Long): Long
}
