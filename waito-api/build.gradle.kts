plugins {
	kotlin("plugin.spring")
	id("org.springframework.boot")
}

dependencies {
	implementation(project(":waito-application"))
	implementation(project(":waito-common:waito-core"))
	implementation(project(":waito-common:waito-mvc"))
	runtimeOnly(project(":waito-common:waito-adapter"))

	implementation("org.springframework.boot:spring-boot-starter-web")
	implementation("org.springframework.boot:spring-boot-starter-actuator")
	implementation("org.springframework.boot:spring-boot-persistence")
	implementation("org.springframework.data:spring-data-jpa")
	implementation("io.micrometer:micrometer-registry-prometheus")
	implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:3.0.3")

	testImplementation("org.springframework.boot:spring-boot-starter-test")
	testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}
