plugins {
	kotlin("plugin.spring")
	id("org.springframework.boot")
}

dependencies {
	implementation(project(":waito-application"))
	implementation(project(":waito-common:waito-core"))
	implementation("org.springframework.boot:spring-boot-starter-web")
	implementation("org.springframework.boot:spring-boot-starter-actuator")
	implementation("org.springframework.boot:spring-boot-persistence")
	implementation("org.springframework.data:spring-data-jpa")
	implementation("org.springframework.boot:spring-boot-starter-kafka")
	implementation("io.micrometer:micrometer-registry-prometheus")

	runtimeOnly(project(":waito-common:waito-adapter"))
}
