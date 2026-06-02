package io.cleralabs.waito.core.validation

object ValidationPatterns {
	val KOREAN_MOBILE_PHONE: Regex = Regex("^01[016789]-?\\d{3,4}-?\\d{4}$")
	val EMAIL: Regex = Regex("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$")
}
