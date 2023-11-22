plugins {
    `kotlin-dsl`
}

dependencies {
    implementation(kotlin("gradle-plugin", version = libs.versions.kotlin.get()))
    implementation("com.diffplug.spotless:spotless-plugin-gradle:6.0.0")
}

repositories {
    mavenCentral()
}
