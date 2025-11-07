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
    override fun apply(project: Project) = with(project.pluginManager) {
        // CRITICAL ORDER: Android → Hilt → Compose → KSP → Base
        apply("com.android.library")
        apply("com.google.dagger.hilt.android")
        apply("org.jetbrains.kotlin.plugin.compose")
        apply("com.google.devtools.ksp")
        apply("genesis.android.base")
    }
}
