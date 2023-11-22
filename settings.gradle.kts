plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version("0.7.0")
}

rootProject.name = "inventory"

include(":core")

@Suppress("UnstableApiUsage")
dependencyResolutionManagement {
    repositories {
        mavenCentral()
    }
}
