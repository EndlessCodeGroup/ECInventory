import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import ru.endlesscode.bukkitgradle.dependencies.codemc
import ru.endlesscode.bukkitgradle.dependencies.jitpack
import ru.endlesscode.bukkitgradle.dependencies.papermc
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
        main.set("ru.endlesscode.inventory.ECInventoryPlugin")
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
    compileOnly(libs.mimic)
    compileOnly(libs.commandapi)

    testImplementation(spigotApi)
    testImplementation(libs.hocon)
}

repositories {
    codemc()
    papermc()
    jitpack {
        content { includeGroup("dev.jorel.CommandAPI") }
    }
}

tasks.withType<KotlinCompile>().configureEach {
    kotlinOptions {
        freeCompilerArgs += "-Xopt-in=kotlinx.serialization.ExperimentalSerializationApi"
    }
}
