import ru.endlesscode.bukkitgradle.dependencies.codemc
import ru.endlesscode.bukkitgradle.dependencies.papermc
import ru.endlesscode.bukkitgradle.dependencies.placeholderApi
import ru.endlesscode.bukkitgradle.dependencies.spigotApi
import ru.endlesscode.bukkitgradle.server.extension.CoreType

plugins {
    `kotlin-convention`
    alias(libs.plugins.serialization)
    alias(libs.plugins.bukkitgradle)
}

bukkit {
    apiVersion = libs.versions.bukkit.get()

    meta {
        apiVersion = "1.18"
        name = "ECInventory"
        main = "ru.endlesscode.inventory.ECInventoryPlugin"
        authors = listOf("osipxd", "Dereku", "EndlessCode Group")
    }

    server {
        coreType = CoreType.SPIGOT
        version = "1.18.2"
        eula = true
    }
}

kotlin {
    explicitApi()
}

dependencies {
    compileOnly(spigotApi)
    compileOnly(libs.serialization.hocon)
    compileOnly(libs.mimic)
    compileOnly(libs.commandapi)
    compileOnly(libs.placeholderapi)
    compileOnly(libs.mysql)
    compileOnly(libs.hikaricp)

    testImplementation(spigotApi)
    testImplementation(libs.serialization.hocon)
    testImplementation(libs.mimic)
}

repositories {
    codemc()
    papermc()
    placeholderApi {
        content { includeGroup("me.clip") }
    }
}

kotlin {
    compilerOptions {
        optIn.add("kotlinx.serialization.ExperimentalSerializationApi")
    }
}
