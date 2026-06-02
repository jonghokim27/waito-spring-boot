plugins {
	kotlin("plugin.spring")
}

dependencies {
	implementation(project(":waito-common:waito-core"))
	implementation("org.springframework.boot:spring-boot-starter-webmvc")
	implementation("com.auth0:java-jwt:4.5.0")

	testImplementation("org.springframework.boot:spring-boot-starter-webmvc-test")
}
