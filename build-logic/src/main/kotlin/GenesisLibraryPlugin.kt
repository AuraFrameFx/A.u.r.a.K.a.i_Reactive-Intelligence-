package plugins

import org.gradle.api.Plugin
import org.gradle.api.Project

/**
 * ═══════════════════════════════════════════════════════════════════════════
 * GENESIS LIBRARY CONVENTION PLUGIN
 * ═══════════════════════════════════════════════════════════════════════════
 *
 * Primary convention plugin for Android library modules.
 * Applies all necessary plugins in the correct order.
 *
 * Usage:
 * ```kotlin
 * plugins {
 *     id("genesis.android.library")
 * }
 * android {
 *     namespace = "dev.aurakai.auraframefx.your.module"
 * }
 * ```
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
