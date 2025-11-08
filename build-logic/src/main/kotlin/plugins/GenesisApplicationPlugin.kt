package plugins

import org.gradle.api.Plugin
import org.gradle.api.Project

/**
 * ═══════════════════════════════════════════════════════════════════════════
 * GENESIS APPLICATION CONVENTION PLUGIN
 * ═══════════════════════════════════════════════════════════════════════════
 *
 * Primary convention plugin for Android application modules (typically :app).
 * Applies all necessary plugins in the correct order.
 *
 * Usage:
 * ```kotlin
 * plugins {
 *     id("genesis.android.application")
 * }
 * android {
 *     namespace = "dev.aurakai.auraframefx"
 *     defaultConfig {
 *         applicationId = "dev.aurakai.auraframefx"
 *     }
 * }
 * ```
 */
class GenesisApplicationPlugin : Plugin<Project> {
    override fun apply(project: Project) = with(project.pluginManager) {
        // CRITICAL ORDER: Android → Hilt → Compose → KSP → Google Services → Firebase → Base
        apply("com.android.application")
        apply("com.google.dagger.hilt.android")
        apply("org.jetbrains.kotlin.plugin.compose")
        apply("com.google.devtools.ksp")
        apply("com.google.gms.google-services")
        apply("com.google.firebase.crashlytics")
        apply("genesis.base")
    }
}
