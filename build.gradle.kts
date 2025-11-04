plugins {
    `java-library`
}

subprojects {
    configurations.all {
        resolutionStrategy {
            // Force the modern JetBrains annotations version
            force("org.jetbrains:annotations:26.0.2-1")
            // Prefer org.jetbrains over com.intellij for annotations
            eachDependency {
                if (requested.group == "com.intellij" && requested.name == "annotations") {
                    useTarget("org.jetbrains:annotations:26.0.2-1")
                    because("Avoid duplicate annotations classes")
                }
            }
        }

        // Exclude the old IntelliJ annotations from all dependencies
        exclude(group = "com.intellij", module = "annotations")
    }

    // Configure Java toolchain for java projects and Kotlin jvm toolchain for Kotlin projects centrally
    // Use Java 24 (supported by Kotlin) as the toolchain language version
    val javaToolchainVersion = 24
}

