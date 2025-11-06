package plugins

import org.gradle.api.Plugin
import org.gradle.api.Project

/**
 * ═══════════════════════════════════════════════════════════════════════════
 * GENESIS LIBRARY CONVENTION PLUGIN
 * ═══════════════════════════════════════════════════════════════════════════
 *
 * The primary convention plugin for Android library modules.
 * Applies to all library modules including :core:*, :feature-*, and extension modules.
 *
 * **CRITICAL PLUGIN APPLICATION ORDER**:
 * The order of plugin application is CRITICAL for proper dependency resolution.
 * Do NOT change this order without testing thoroughly.
 *
 * 1. com.android.library               - Android Library Plugin (MUST be first)
 * 2. com.google.dagger.hilt.android    - Hilt Dependency Injection
 * 3. org.jetbrains.kotlin.plugin.compose - Compose Compiler (for UI libraries)
 * 4. com.google.devtools.ksp           - Kotlin Symbol Processing
 *
 * This plugin automatically applies ALL plugins needed for a standard Android
 * library with Hilt, Compose, and KSP support.
 *
 * **Usage in module build.gradle.kts**:
 * ```kotlin
 * plugins {
 *     id("genesis.android.library")
 *     // Optionally add specialized plugins:
 *     // id("genesis.android.yukihook")  // For Xposed modules
 *     // id("genesis.android.room")      // For Room database
 * }
 * ```
 *
 * **What you DON'T need to add**:
 * - android { } block configuration (handled by genesis.android.base)
 * - Individual plugin declarations (all applied automatically)
 * - Common dependencies (provided by genesis.android.base)
 * - Hilt setup (automatically configured)
 * - Compose setup (automatically configured)
 *
 * **What you DO need to add**:
 * - android.namespace = "your.package.name"
 * - Module-specific dependencies
 * - project(...) dependencies for inter-module communication
 *
 * **For specialized modules, also apply**:
 * - `genesis.android.yukihook` - If this is an Xposed/system hook module
 * - `genesis.android.room` - If this uses Room database
 * - Apply AFTER this plugin for proper configuration layering
 *
 * @since Genesis Protocol 2.0
 * @see GenesisApplicationPlugin for application modules
 * @see GenesisBasePlugin for base configuration
 */
class GenesisLibraryPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target.pluginManager) {
            // Apply plugins in the CRITICAL order specified above

            // 1. Android Library - MUST be first
            apply("com.android.library")

            // 2. Hilt - Dependency Injection (most libraries use DI)
            apply("com.google.dagger.hilt.android")

            // 3. Compose Compiler - UI framework (most libraries have UI components)
            apply("org.jetbrains.kotlin.plugin.compose")

            // 4. KSP - Annotation Processing (required by Hilt, Room, Moshi, etc.)
            apply("com.google.devtools.ksp")

            // Now apply our base configuration
            apply("genesis.android.base")
        }

        // Log successful application (helpful for debugging)
        target.logger.info("✓ Genesis Library Plugin applied to ${target.name}")
    }
}
