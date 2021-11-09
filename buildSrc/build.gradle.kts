plugins {
    `kotlin-dsl`
}

dependencies {
    implementation(kotlin("gradle-plugin", version = "1.5.31"))
    implementation("com.diffplug.spotless:spotless-plugin-gradle:6.0.0")
}

repositories {
    mavenCentral()
}
