import org.jetbrains.kotlin.gradle.dsl.KotlinVersion

plugins {
    kotlin("jvm")
    id("com.diffplug.spotless")
}

description = rootProject.description

kotlin {
    jvmToolchain(17)

    compilerOptions {
        apiVersion = KotlinVersion.KOTLIN_1_9
        languageVersion = KotlinVersion.KOTLIN_1_9
        optIn.add("kotlin.RequiresOptIn")
    }
}

// TODO: Remove after it will be fixed in BukkitGradle
//   https://github.com/EndlessCodeGroup/BukkitGradle/issues/60
afterEvaluate {
    java {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
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
    compileOnly(kotlin("stdlib"))
    testImplementation(kotlin("stdlib"))
    testImplementation(kotlin("test-junit5"))
    testImplementation(platform("org.junit:junit-bom:5.9.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    testImplementation("io.mockk:mockk:1.12.7")

    val kotestVersion = "5.4.2"
    testImplementation("io.kotest:kotest-runner-junit5:$kotestVersion")
    testImplementation("io.kotest:kotest-framework-datatest:$kotestVersion")
    testImplementation("io.kotest:kotest-assertions-core:$kotestVersion")
}
