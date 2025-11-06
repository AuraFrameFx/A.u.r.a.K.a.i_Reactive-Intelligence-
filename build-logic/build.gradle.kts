
plugins {
    `kotlin-dsl`
}

java {
    toolchain {
        // UPDATED: Java 25 toolchain for development environment
        languageVersion.set(JavaLanguageVersion.of(25))
    }
}

dependencies {
    // Plugin dependencies for convention plugins
    // These allow the convention plugins to apply Android, Kotlin, Hilt, KSP, and Google Services plugins
    implementation(libs.gradle.plugin)
    implementation(libs.kotlin.gradle.plugin)
    implementation(libs.hilt.gradle.plugin)
    implementation(libs.ksp.gradle.plugin)
    implementation(libs.google.services.gradle.plugin)

    // Kotlin Serialization plugin is bundled in kotlin-gradle-plugin, but we need to ensure it's accessible
    // The serialization plugin is applied via kotlin-gradle-plugin, no separate dependency needed
}
// ═══════════════════════════════════════════════════════════════════════════
// Binary Kotlin Class Plugins Registration
// ═══════════════════════════════════════════════════════════════════════════
//
// These are the PRIMARY convention plugins that modules should use.
// They are Kotlin class plugins (not precompiled scripts) for maximum control
// over plugin application order.
//
gradlePlugin {
    plugins {
        // Primary application plugin - for :app module
        register("genesisAndroidApplication") {
            id = "genesis.android.application"
            implementationClass = "plugins.GenesisApplicationPlugin"
            displayName = "Genesis Android Application Plugin"
            description = "Applies Android Application plugin with Hilt, KSP, Compose, Serialization, and Firebase"
        }

        // Primary library plugin - for all library modules
        register("genesisAndroidLibrary") {
            id = "genesis.android.library"
            implementationClass = "plugins.GenesisLibraryPlugin"
            displayName = "Genesis Android Library Plugin"
            description = "Applies Android Library plugin with Hilt, Compose, and KSP"
        }

        // Base configuration plugin - applied automatically by application and library plugins
        register("genesisAndroidBase") {
            id = "genesis.android.base"
            implementationClass = "plugins.GenesisBasePlugin"
            displayName = "Genesis Android Base Plugin"
            description = "Foundational configuration for all Android modules (SDK versions, Kotlin, dependencies)"
        }
    }
}

// ═══════════════════════════════════════════════════════════════════════════
// CORRECT USAGE EXAMPLES
// ═══════════════════════════════════════════════════════════════════════════
//
// For :app module:
//   plugins {
//       id("genesis.android.application")  // All-in-one: Android, Hilt, KSP, Compose, Serialization, Firebase
//   }
//
// For standard library module:
//   plugins {
//       id("genesis.android.library")  // All-in-one: Android, Hilt, Compose, KSP
//   }
//
// For YukiHook/Xposed module:
//   plugins {
//       id("genesis.android.library")   // Base library with Hilt, Compose, KSP
//       id("genesis.android.yukihook")  // Add YukiHook/Xposed support
//   }
//
// For Room database module:
//   plugins {
//       id("genesis.android.library")  // Base library
//       id("genesis.android.room")     // Add Room Database
//   }
//
// ═══════════════════════════════════════════════════════════════════════════
