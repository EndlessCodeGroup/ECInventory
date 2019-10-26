// Apply shared configuration to subprojects
plugins {
    id("com.github.ben-manes.versions") version "0.27.0"
}

subprojects {
    kotlinProject()
}
