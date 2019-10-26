plugins {
    id("ru.endlesscode.bukkitgradle")
    id("com.github.johnrengelman.shadow") version "5.1.0"
}

bukkit {
    version = "1.13"

    meta {
        setName(rootProject.name)
        setMain("ru.endlesscode.rpginventory.RPGInventoryPlugin")
        setAuthors(listOf("osipxd", "Dereku", "EndlessCode Group"))
    }

    run {
        eula = true
    }
}

// Replace 'jar' task by 'shadowJar'
tasks.jar.get().enabled = false
tasks.assemble.get().dependsOn(tasks.shadowJar)

tasks.shadowJar.configure {
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
    compileOnly(bukkit)

    // Runtime dependencies will be bundled into the output jar
    implementation(deps.hocon) {
        // Guava already in Bukkit
        exclude(group = "com.google.guava")
    }
}
