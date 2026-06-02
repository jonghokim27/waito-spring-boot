plugins {
	kotlin("plugin.spring")
}

dependencies {
	implementation(project(":waito-domain"))
	implementation(project(":waito-common:waito-core"))
}
