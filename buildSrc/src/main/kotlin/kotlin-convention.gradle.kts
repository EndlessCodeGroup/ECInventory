plugins {
    kotlin("jvm")
    id("com.diffplug.spotless")
}

description = rootProject.description

kotlin {
    jvmToolchain(17)

    compilerOptions {
        optIn.add("kotlin.RequiresOptIn")
    }
}

tasks.test {
    useJUnitPlatform()
}

spotless {
    ratchetFrom("origin/develop")

    kotlin {
        leadingTabsToSpaces()
        endWithNewline()
        licenseHeaderFile(rootProject.file("spotless.license.kt"))
    }
}

dependencies {
    compileOnly(kotlin("stdlib"))
    testImplementation(kotlin("stdlib"))
    testImplementation(kotlin("test-junit5"))
    testImplementation(platform("org.junit:junit-bom:5.10.1"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    testImplementation("io.mockk:mockk:1.13.8")

    val kotestVersion = "5.8.0"
    testImplementation("io.kotest:kotest-runner-junit5:$kotestVersion")
    testImplementation("io.kotest:kotest-framework-datatest:$kotestVersion")
    testImplementation("io.kotest:kotest-assertions-core:$kotestVersion")
}
