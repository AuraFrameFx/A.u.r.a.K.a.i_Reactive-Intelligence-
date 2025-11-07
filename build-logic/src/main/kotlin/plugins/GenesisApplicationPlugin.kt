package plugins

import org.gradle.api.Plugin
import org.gradle.api.Project

/**
 * Android app conventions for Aurakai Canary.
 * Order (critical): Android → Kotlin Android → Hilt → KSP → Compose → Serialization → Google Services
 */
class GenesisApplicationPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target.pluginManager) {
            apply("genesis.base")
            apply("com.android.application")
            apply("org.jetbrains.kotlin.android")          // builtInKotlin=false path
            apply("com.google.dagger.hilt.android")
            apply("com.google.devtools.ksp")
            apply("org.jetbrains.kotlin.plugin.compose")   // Compose compiler plugin (tracks Kotlin)
            apply("org.jetbrains.kotlin.plugin.serialization")
            apply("com.google.gms.google-services")        // keep last
        }

        // Configure Kotlin compiler options for Android
        target.plugins.withId("org.jetbrains.kotlin.android") {
            target.extensions.configure(org.jetbrains.kotlin.gradle.dsl.KotlinAndroidProjectExtension::class.java) {
                compilerOptions.jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.fromTarget("24"))
            }
        }

        // Configure Kotlin compiler options for JVM
        target.plugins.withId("org.jetbrains.kotlin.jvm") {
            target.extensions.configure(org.jetbrains.kotlin.gradle.dsl.KotlinJvmProjectExtension::class.java) {
                compilerOptions.jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.fromTarget("24"))
            }
        }
    }
}
