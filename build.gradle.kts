// Apply shared configuration to subprojects
plugins {
    id("com.github.ben-manes.versions") version "0.29.0"
}

subprojects {
    apply(plugin = "kotlin-convention")
}
