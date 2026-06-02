package io.cleralabs.waito.domain.user

import io.cleralabs.waito.core.enums.Gender
import io.cleralabs.waito.core.validation.requireInput
import io.cleralabs.waito.core.validation.ValidationPatterns
import io.cleralabs.waito.domain.common.BaseEntity
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.Index
import jakarta.persistence.Table
import java.time.LocalDate

@Entity
@Table(
	name = "users",
	indexes = [
		Index(name = "uk_users_phone_number", columnList = "phone_number", unique = true),
		Index(name = "uk_users_email", columnList = "email", unique = true),
	],
)
class User(
	@Column(name = "phone_number", nullable = false, length = 20, unique = true)
	var phoneNumber: String,

	@Column(name = "email", nullable = false, length = 255, unique = true)
	var email: String,

	@Column(name = "birth_date", nullable = false)
	var birthDate: LocalDate,

	@Column(name = "name", nullable = false, length = 50)
	var name: String,

	@Enumerated(EnumType.STRING)
	@Column(name = "gender", nullable = false, length = 20)
	var gender: Gender,
) : BaseEntity() {

	init {
		validate()
	}

	fun updateProfile(
		phoneNumber: String = this.phoneNumber,
		email: String = this.email,
		birthDate: LocalDate = this.birthDate,
		name: String = this.name,
		gender: Gender = this.gender,
	) {
		this.phoneNumber = phoneNumber
		this.email = email
		this.birthDate = birthDate
		this.name = name
		this.gender = gender

		validate()
	}

	private fun validate() {
		requireInput(phoneNumber.matches(ValidationPatterns.KOREAN_MOBILE_PHONE), "user.phone-number-invalid")
		requireInput(email.matches(ValidationPatterns.EMAIL), "user.email-invalid")
		requireInput(!birthDate.isAfter(LocalDate.now()), "user.birth-date-invalid")
		requireInput(name.isNotBlank(), "user.name-blank")
		requireInput(name.length <= 50, "user.name-too-long")
	}
}
