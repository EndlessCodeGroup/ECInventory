import ru.endlesscode.bukkitgradle.dependencies.spigotApi

plugins {
    id("ru.endlesscode.bukkitgradle") version "0.10.0"
    id("com.github.johnrengelman.shadow") version "7.1.0"
}

bukkit {
    apiVersion = "1.17.1"

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

tasks.shadowJar {
    tasks.jar.get().enabled = false
    tasks.assemble.get().dependsOn(this)

    // Understandable filename
    archiveBaseName.set("$base.name-$project.name")
    archiveClassifier.set("")

    // Avoid conflicts with others
    val shadedPackage = "ru.endlesscode.rpginventory.shaded"
    relocate("com.typesafe.config", "$shadedPackage.config")
    relocate("ninja.configurate", "$shadedPackage.configurate")
}

dependencies {
    api(project(":core:api"))
    compileOnly(spigotApi)

    // Runtime dependencies will be bundled into the output jar
    implementation(deps.hocon) {
        // Guava already in Bukkit
        exclude(group = "com.google.guava")
    }
}
