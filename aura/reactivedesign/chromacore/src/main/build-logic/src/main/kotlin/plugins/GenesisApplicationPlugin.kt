package plugins

import org.gradle.api.Plugin
import org.gradle.api.Project

/**
 * ===================================================================
 * GENESIS APPLICATION CONVENTION PLUGIN
 * ===================================================================
 *
 * The primary convention plugin for Android application modules.
 *
 * Plugin Application Order (Critical!):
 * 1. Android Application
 * 2. Hilt (Dependency Injection)
 * 3. KSP (Annotation Processing)
 * 4. Compose Compiler
 * 5. Google Services (Firebase)
 *
 * @since Genesis Protocol 1.0
 */
class GenesisApplicationPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        // Apply plugins directly to the plugin manager for clarity and compatibility
        // with strict Gradle versions.
        with(target.pluginManager) {
            apply("com.android.tools.build:gradle:9.0.0-alpha11")
            apply("com.google.dagger:hilt-android-gradle-plugin:2.57.2")
            apply("com.google.devtools.ksp:com.google.devtools.ksp.gradle.plugin:2.3.0")
            apply("org.jetbrains.kotlin:kotlin-gradle-plugin:2.3.0-Beta1")
            apply("org.jetbrains.kotlin:kotlin-serialization:2.3.0-Beta1")
            apply("org.jetbrains.kotlin:kotlin-allopen:2.3.0-Beta1")
            apply("com.google.firebase:firebase-crashlytics-buildtools:3.0.6")
            apply("com.google.gms.google-services:com.google.gms.google-services.gradle.plugin:4.4.4")
        }
    }
}
