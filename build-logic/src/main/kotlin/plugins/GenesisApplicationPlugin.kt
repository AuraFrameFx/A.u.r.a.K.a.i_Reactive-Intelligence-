package plugins

import com.android.build.api.dsl.ApplicationExtension
import org.gradle.api.JavaVersion
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.apply
import org.gradle.kotlin.dsl.getByType
import org.gradle.kotlin.dsl.withType
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

/**
 * ===================================================================
 * GENESIS APPLICATION CONVENTION PLUGIN
 * ===================================================================
 *
 * The primary convention plugin for Android application modules.
 *
 * This plugin configures:
 * - Android application plugin and extensions
 * - Kotlin Android support
 * - Hilt dependency injection
 * - KSP annotation processing
 * - Jetpack Compose
 * - Google Services (Firebase)
 * - Consistent build configuration across app modules
 *
 * Plugin Application Order (Critical!):
 * 1. genesis.android.base (inherits JVM/Kotlin settings)
 * 2. com.android.application
 * 3. org.jetbrains.kotlin.android
 * 4. com.google.dagger.hilt.android
 * 5. com.google.devtools.ksp
 * 6. org.jetbrains.kotlin.plugin.compose (New plugin for Kotlin 2.0+)
 * 7. com.google.gms.google-services
 *
 * @since Genesis Protocol 2.0 (Modernized)
 */
class GenesisApplicationPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            pluginManager.apply {
                // Apply base Genesis conventions first
                apply("com.android.base")

                // Apply Android and Kotlin plugins
                apply("com.android.application")
                apply("org.jetbrains.kotlin.android")
                apply("com.google.dagger.hilt.android")
                apply("com.google.devtools.ksp")
                apply("org.jetbrains.kotlin.plugin.compose") // Apply the new compose compiler plugin
                apply("com.google.gms.google-services")
            }

            // Configure Android extension
            val extension = extensions.getByType<ApplicationExtension>()
            extension.apply {
                // Use a recent compile SDK version
                compileSdk = 34 // Android 14

                defaultConfig {
                    applicationId = "dev.aurakai.auraframefx"
                    minSdk = 24 // Use a modern minimum SDK, 24 is a common modern baseline
                    targetSdk = 34
                    versionCode = 1
                    versionName = "1.0"

                    testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
                    vectorDrawables {
                        useSupportLibrary = true
                    }
                }

                buildTypes {
                    release {
                        isMinifyEnabled = true
                        proguardFiles(
                            getDefaultProguardFile("proguard-android-optimize.txt"),
                            "proguard-rules.pro"
                        )
                    }
                }

                // Modern compile options with a modern Java version (e.g., 17)
                compileOptions {
                    sourceCompatibility = JavaVersion.VERSION_17
                    targetCompatibility = JavaVersion.VERSION_17
                    // Consider if core library desugaring is still needed with minSdk 24
                    isCoreLibraryDesugaringEnabled = false
                }

                buildFeatures {
                    compose = true
                    // buildConfig is true by default in recent AGP versions, but explicit is fine
                    buildConfig = true
                }

                // *** REMOVED: composeOptions is not needed with the new plugin ***
                // The version is now managed by the applied plugin in the plugins block

                packaging {
                    resources {
                        excludes += "/META-INF/{AL2.0,LGPL2.1}"
                    }
                }
            }

            // Set Kotlin JVM target for Android
            tasks.withType<KotlinCompile>().configureEach {

            }
        }
    }
}
