package io.cleralabs.waito.domain.common

import jakarta.persistence.Column
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.MappedSuperclass
import org.hibernate.annotations.CreationTimestamp
import org.hibernate.annotations.UpdateTimestamp
import java.time.LocalDateTime

@MappedSuperclass
abstract class BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id", nullable = false, updatable = false)
	var id: Long = 0L
		protected set

	@CreationTimestamp
	@Column(name = "created_at", nullable = false, updatable = false)
	lateinit var createdAt: LocalDateTime
		protected set

	@UpdateTimestamp
	@Column(name = "updated_at", nullable = false)
	lateinit var updatedAt: LocalDateTime
		protected set
}
