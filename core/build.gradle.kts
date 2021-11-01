import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import ru.endlesscode.bukkitgradle.dependencies.spigotApi

plugins {
    `kotlin-convention`
    alias(libs.plugins.serialization)
    alias(libs.plugins.bukkitgradle)
}

bukkit {
    apiVersion = libs.versions.bukkit.get()

    meta {
        name.set(rootProject.name)
        main.set("ru.endlesscode.rpginventory.RPGInventoryPlugin")
        authors.set(listOf("osipxd", "Dereku", "EndlessCode Group"))
    }

    server {
        setCore("paper")
        eula = true
    }
}

dependencies {
    compileOnly(spigotApi)
    compileOnly(libs.hocon)

    testImplementation(spigotApi)
    testImplementation(libs.hocon)
}

tasks.withType<KotlinCompile>().configureEach {
    kotlinOptions {
        freeCompilerArgs += "-Xopt-in=kotlinx.serialization.ExperimentalSerializationApi"
    }
}
