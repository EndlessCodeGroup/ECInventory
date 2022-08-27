import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm")
    id("com.diffplug.spotless")
}

description = rootProject.description

java {
    sourceCompatibility = JavaVersion.VERSION_11
    targetCompatibility = JavaVersion.VERSION_11
}

tasks.withType<KotlinCompile>().configureEach {
    kotlinOptions {
        jvmTarget = "11"
        apiVersion = "1.7"
        languageVersion = "1.7"
        freeCompilerArgs += "-opt-in=kotlin.RequiresOptIn"
    }
}

tasks.test {
    useJUnitPlatform()
    testLogging {
        events("passed", "skipped", "failed")
    }
}

spotless {
    ratchetFrom("origin/develop")

    kotlin {
        indentWithSpaces()
        endWithNewline()
        licenseHeaderFile(rootProject.file("spotless.license.kt"))
    }
}

dependencies {
    compileOnly(kotlin("stdlib-jdk8"))
    testImplementation(kotlin("stdlib-jdk8"))
    testImplementation(kotlin("test-junit5"))
    testImplementation(platform("org.junit:junit-bom:5.9.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    testImplementation("io.mockk:mockk:1.12.7")

    val kotestVersion = "5.4.2"
    testImplementation("io.kotest:kotest-runner-junit5:$kotestVersion")
    testImplementation("io.kotest:kotest-framework-datatest:$kotestVersion")
    testImplementation("io.kotest:kotest-assertions-core:$kotestVersion")
}

repositories {
    mavenCentral()
}
