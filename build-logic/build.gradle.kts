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
    // These allow the convention plugins to apply Android, Kotlin, Hilt, and KSP plugins
    implementation(libs.gradle.plugin)
    implementation(libs.kotlin.gradle.plugin)
    implementation(libs.hilt.gradle.plugin)
    implementation(libs.ksp.gradle.plugin)
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
// ⚠️ DEPRECATED: Old Precompiled Script Plugins
// ═══════════════════════════════════════════════════════════════════════════
//
// The following precompiled script plugins (.gradle.kts files) are DEPRECATED
// and should NO LONGER be used directly in modules:
//
//   DEPRECATED (DO NOT USE):
//   • genesis.android.application.gradle.kts - Use genesis.android.application instead
//   • genesis.android.library.gradle.kts     - Use genesis.android.library instead
//   • genesis.android.base.gradle.kts        - Auto-applied by application/library plugins
//
// These files will be removed in a future version.
//
// ═══════════════════════════════════════════════════════════════════════════
// Specialized Precompiled Script Plugins (Still Available)
// ═══════════════════════════════════════════════════════════════════════════
//
// The following specialized plugins are still available as precompiled scripts:
//
//   SPECIALIZED (apply AFTER genesis.android.library):
//   • genesis.android.yukihook    - YukiHook/Xposed framework support
//   • genesis.android.room        - Room Database
//   • genesis.kotlin.jvm          - Pure Kotlin JVM modules (no Android)
//
// ⚠️ The following plugins are now INCLUDED in the main plugins and should NOT be applied separately:
//   • genesis.android.hilt        - Included in genesis.android.application and genesis.android.library
//   • genesis.android.compose     - Included in genesis.android.application and genesis.android.library
//
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
