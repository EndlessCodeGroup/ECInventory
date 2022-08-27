plugins {
    `kotlin-dsl`
}

dependencies {
    implementation(kotlin("gradle-plugin", version = "1.7.10"))
    implementation("com.diffplug.spotless:spotless-plugin-gradle:6.0.0")
}

repositories {
    mavenCentral()
}
