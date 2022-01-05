import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import ru.endlesscode.bukkitgradle.dependencies.*

plugins {
    `kotlin-convention`
    alias(libs.plugins.serialization)
    alias(libs.plugins.bukkitgradle)
}

bukkit {
    apiVersion = libs.versions.bukkit.get()

    meta {
        apiVersion.set("1.16")
        name.set("ECInventory")
        main.set("ru.endlesscode.inventory.ECInventoryPlugin")
        authors.set(listOf("osipxd", "Dereku", "EndlessCode Group"))
    }

    server {
        setCore("paper")
        eula = true
    }
}

kotlin {
    explicitApi()
}

dependencies {
    compileOnly(spigotApi)
    compileOnly(libs.hocon)
    compileOnly(libs.mimic)
    compileOnly(libs.commandapi)
    compileOnly(libs.placeholderapi)
    compileOnly(libs.mysql)
    compileOnly(libs.hikaricp)

    testImplementation(spigotApi)
    testImplementation(libs.hocon)
    testImplementation(libs.mimic)
}

repositories {
    codemc()
    papermc()
    placeholderApi {
        content { includeGroup("me.clip") }
    }
    jitpack {
        content { includeGroup("dev.jorel.CommandAPI") }
    }
}

tasks.withType<KotlinCompile>().configureEach {
    kotlinOptions {
        freeCompilerArgs += "-Xopt-in=kotlinx.serialization.ExperimentalSerializationApi"
    }
}
