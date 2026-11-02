import ru.endlesscode.bukkitgradle.dependencies.codemc
import ru.endlesscode.bukkitgradle.dependencies.papermc
import ru.endlesscode.bukkitgradle.dependencies.placeholderApi
import ru.endlesscode.bukkitgradle.dependencies.spigot
import ru.endlesscode.bukkitgradle.dependencies.spigotApi
import xyz.jpenilla.resourcefactory.bukkit.Permission

plugins {
    `kotlin-convention`
    alias(libs.plugins.serialization)
    alias(libs.plugins.bukkitgradle)
}

bukkit {
    apiVersion = libs.versions.bukkit.get()

    plugin {
        apiVersion = "1.18"
        name = "ECInventory"
        main = "ru.endlesscode.inventory.ECInventoryPlugin"
        authors = listOf("osipxd", "Dereku", "EndlessCode Group")
        depend = listOf("Mimic", "CommandAPI")
        // We want to use ProRPGItems' ItemsRegistry.
        softDepend = listOf("PlaceholderAPI", "ProRPGItems")
        libraries.addAll(
            "org.jetbrains.kotlin:kotlin-stdlib:${libs.versions.kotlin.get()}",
            libs.serialization.hocon.get().toString(),
            libs.hikaricp.get().toString(),
        )
        permissions {
            register("ecinventory.open") { default = Permission.Default.TRUE }
            register("ecinventory.open.others") { default = Permission.Default.OP }
            register("ecinventory.reload") { default = Permission.Default.OP }
        }
    }

    server {
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
    compileOnly(libs.commandapi.kotlin)
    compileOnly(libs.placeholderapi)
    compileOnly(libs.mysql)
    compileOnly(libs.hikaricp)

    testImplementation(spigotApi)
    testImplementation(libs.serialization.hocon)
    testImplementation(libs.mimic)
}

repositories {
    mavenCentral()
    spigot()
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
