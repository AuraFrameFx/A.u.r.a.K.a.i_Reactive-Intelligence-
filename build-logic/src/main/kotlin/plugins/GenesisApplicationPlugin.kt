package plugins

import org.gradle.api.Plugin
import org.gradle.api.Project

/**
 * ═══════════════════════════════════════════════════════════════════════════
 * GENESIS APPLICATION CONVENTION PLUGIN
 * ═══════════════════════════════════════════════════════════════════════════
 *
 * The primary convention plugin for Android application modules (typically :app).
 *
 * **CRITICAL PLUGIN APPLICATION ORDER**:
 * The order of plugin application is CRITICAL for proper dependency resolution
 * and build configuration. Do NOT change this order without testing thoroughly.
 *
 * 1. com.android.application         - Android Application Plugin (MUST be first)
 * 2. com.google.dagger.hilt.android  - Hilt Dependency Injection
 * 3. com.google.devtools.ksp         - Kotlin Symbol Processing (required by Hilt)
 * 4. org.jetbrains.kotlin.plugin.compose - Compose Compiler
 * 5. org.jetbrains.kotlin.plugin.serialization - Kotlin Serialization
 * 6. com.google.gms.google-services  - Google Services/Firebase (MUST be last)
 *
 * This plugin automatically applies ALL plugins needed for a standard Android
 * application with Hilt, Compose, KSP, Serialization, and Firebase.
 *
 * **Usage in module build.gradle.kts**:
 * ```kotlin
 * plugins {
 *     id("genesis.android.application")
 * }
 * ```
 *
 * **What you DON'T need to add**:
 * - android { } block configuration (handled by genesis.android.base)
 * - Individual plugin declarations (all applied automatically)
 * - Common dependencies (provided by genesis.android.base)
 *
 * **What you DO need to add**:
 * - android.namespace = "your.package.name"
 * - android.defaultConfig.applicationId = "your.app.id"
 * - Module-specific dependencies
 *
 * @since Genesis Protocol 2.0
 * @see GenesisLibraryPlugin for library modules
 * @see GenesisBasePlugin for base configuration
 */
class GenesisApplicationPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target.pluginManager) {
            // Apply plugins in the CRITICAL order specified above

            // 1. Android Application - MUST be first
            apply("com.android.application")

            // 2. Hilt - Dependency Injection
            apply("com.google.dagger.hilt.android")

            // 3. KSP - Annotation Processing (required by Hilt, Room, Moshi)
            apply("com.google.devtools.ksp")

            // 4. Compose Compiler - Modern UI framework
            apply("org.jetbrains.kotlin.plugin.compose")

            // 5. Kotlin Serialization - JSON/data serialization
            apply("org.jetbrains.kotlin.plugin.serialization")

            // 6. Google Services - Firebase integration (MUST be last)
            apply("com.google.gms.google-services")

            // Now apply our base configuration that sets up Android SDK, Kotlin, etc.
            apply("genesis.android.base")
        }

        // Log successful application (helpful for debugging)
        target.logger.info("✓ Genesis Application Plugin applied to ${target.name}")
    }
}
