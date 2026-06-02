plugins {
	kotlin("plugin.spring")
}

dependencies {
	implementation(project(":waito-common:waito-core"))
	implementation("org.springframework:spring-context")
	implementation("org.springframework.boot:spring-boot-starter-data-redis")
	implementation("org.springframework.boot:spring-boot-starter-kafka")
	implementation("io.micrometer:micrometer-core")
}
