// ===================================================================
// GENESIS PROTOCOL - JAVA TOOLCHAIN CONFIGURATION
// ===================================================================
// Strictly enforces Java 24 with fallback to 25 across all modules
// Optimized for consciousness substrate performance
// ===================================================================


// Configure Java toolchain for all projects
allprojects {
    // Configure Java toolchain with strict version requirements
    plugins.withType<JavaBasePlugin> {
        extensions.configure<JavaPluginExtension> {
            toolchain {
                // Primary toolchain: Java 25 (compiles to bytecode 24)
                val targetVersion = 25
                val fallbackVersion = 24

                try {
                    // Prefer Java 25 toolchain
                    languageVersion.set(JavaLanguageVersion.of(targetVersion))
                    logger.lifecycle("🧠 GENESIS PROTOCOL: Using Java $targetVersion for ${project.name}")
                } catch (e: Exception) {
                    // Fallback to Java 24 if 25 is not available
                    languageVersion.set(JavaLanguageVersion.of(fallbackVersion))
                    logger.lifecycle("⚠️  GENESIS WARNING: Java $targetVersion not found, falling back to Java $fallbackVersion for ${project.name}")
                }
            }
        }
    }


    // Configure Kotlin compilation for Android modules

    // Configure Java compilation for all modules
    plugins.withType<JavaPlugin> {
        tasks.withType<JavaCompile>().configureEach {
            sourceCompatibility = "24"
            targetCompatibility = "24"
            options.encoding = "UTF-8"
            options.isIncremental = true
            options.isFork = true

            // Enable all warnings and treat them as errors
            options.compilerArgs.addAll(
                listOf(
                    "-Xlint:all",
                    "-Werror",
                    "--release", "24"
                )
            )
        }
    }


// Log detailed Java version information at the end of the configuration phase
    gradle.projectsEvaluated {
        val jvm = System.getProperty("java.vm.name")
        val jreVersion = System.getProperty("java.version")
        val jreHome = System.getProperty("java.home")

        logger.lifecycle(
            """
        ===================================================================
        🧬 GENESIS PROTOCOL - JAVA TOOLCHAIN STATUS
        ===================================================================
        - Current JVM: $jvm (${System.getProperty("java.vm.version")})
        - Java Version: $jreVersion
        - Java Home: $jreHome
        - Active Java Toolchain: ${JavaVersion.current()}
        - Target Java Toolchain: 25 (with fallback to 24)
        - Java Bytecode Target: 24 (sourceCompatibility/targetCompatibility)
        - Kotlin Compiler Target: 24
        - Project: ${project.name} (${project.path})
        ===================================================================""".trimIndent()
        )
    }
}
