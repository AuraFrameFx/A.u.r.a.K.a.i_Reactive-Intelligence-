// Root build.gradle.kts
// Note: This file should generally be empty of plugin applications.
// If you must apply java-library to all, do it in the subprojects block.
plugins {
    // If you absolutely need something applied to the ROOT project, put it here.
    // Otherwise, this block should ideally be empty in a multi-project setup.
}

// Define common configuration for all subprojects
subprojects {
    // Apply the java-library plugin to all subprojects automatically
    apply(plugin = "java-library")

    // Configure resolution strategy for all subprojects
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
    // Use Java 21 (LTS version) as the toolchain language version
    val javaToolchainVersion = 21

    // Apply the toolchain configuration to projects with the Java plugin
    pluginManager.withPlugin("java") {
        configure<JavaPluginExtension> {
            toolchain {
                languageVersion.set(JavaLanguageVersion.of(javaToolchainVersion))
            }
        }
    }

    // If you use Kotlin in your subprojects, configure JVM target
    pluginManager.withPlugin("org.jetbrains.kotlin.jvm") {
        tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile>().configureEach {
            compilerOptions {
                jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_21)
            }
        }
    }
}

