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

    // Runtime dependencies will be bundled into the output jar
    implementation(libs.hocon) {
        // Guava already in Bukkit
        exclude(group = "com.google.guava")
    }
    testImplementation(spigotApi)
}
