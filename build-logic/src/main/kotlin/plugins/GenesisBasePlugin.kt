package plugins

import com.android.build.api.dsl.CommonExtension
import org.gradle.api.JavaVersion
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.artifacts.VersionCatalog
import org.gradle.api.artifacts.VersionCatalogsExtension
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.dependencies
import org.gradle.kotlin.dsl.getByType
import org.gradle.kotlin.dsl.withType
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

/**
 * ═══════════════════════════════════════════════════════════════════════════
 * GENESIS BASE CONVENTION PLUGIN
 * ═══════════════════════════════════════════════════════════════════════════
 *
 * The foundational configuration layer for ALL Android modules.
 * This plugin establishes the baseline settings that every module needs.
 *
 * **Applied automatically by**:
 * - GenesisApplicationPlugin
 * - GenesisLibraryPlugin
 *
 * **DO NOT apply this plugin directly in modules!**
 * It is an internal plugin used by the application and library plugins.
 *
 * **Configuration Provided**:
 * - Kotlin Android plugin
 * - Android SDK versions (compileSdk, minSdk, targetSdk)
 * - Java/Kotlin compatibility (Java 24 bytecode target)
 * - Core library desugaring
 * - Universal dependencies (Kotlin Coroutines, Timber, etc.)
 * - Testing framework setup
 * - Packaging options
 * - Progressive Kotlin mode
 *
 * **Java Bytecode Target Strategy**:
 * - Java 25 toolchain (development environment)
 * - Java 24 bytecode target (runtime compatibility with API 34+)
 * - Aligns with project min-sdk 34 (Android 14)
 *
 * @since Genesis Protocol 2.0
 * @see GenesisApplicationPlugin
 * @see GenesisLibraryPlugin
 */
class GenesisBasePlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            // Apply Kotlin Android plugin (required for all Android modules)
            pluginManager.apply("org.jetbrains.kotlin.android")

            // Get access to version catalog
            val libs = extensions.getByType<VersionCatalogsExtension>().named("libs")

            // Configure Android block for both app and library modules
            extensions.configure<CommonExtension<*, *, *, *, *, *>> {
                configureAndroid(libs)
            }

            // Configure Kotlin compiler options
            tasks.withType<KotlinCompile>().configureEach {
                configureKotlin()
            }

            // Add universal dependencies
            dependencies {
                addUniversalDependencies(libs)
            }

            logger.info("✓ Genesis Base Plugin configured for ${target.name}")
        }
    }

    /**
     * Configures the Android block with SDK versions, Java compatibility, etc.
     */
    private fun CommonExtension<*, *, *, *, *, *>.configureAndroid(libs: VersionCatalog) {
        // SDK Versions
        compileSdk = libs.findVersion("compile-sdk").get().toString().toInt()

        defaultConfig {
            minSdk = libs.findVersion("min-sdk").get().toString().toInt()
            testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        }

        // CRITICAL: Java/Kotlin Bytecode Target
        // Java 25 toolchain for development, Java 24 bytecode for Android 14+ runtime
        compileOptions {
            sourceCompatibility = JavaVersion.VERSION_24
            targetCompatibility = JavaVersion.VERSION_24
            isCoreLibraryDesugaringEnabled = true
        }

        // Packaging options to avoid conflicts
        packaging {
            resources {
                excludes += "/META-INF/{AL2.0,LGPL2.1}"
                excludes += "/META-INF/LICENSE.md"
                excludes += "/META-INF/LICENSE-notice.md"
            }
        }
    }

    /**
     * Configures Kotlin compiler options
     */
    private fun KotlinCompile.configureKotlin() {
        compilerOptions {
            // Align with Java bytecode target
            jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_24)

            // Enable progressive mode for stricter checks
            progressiveMode.set(true)

            // Compiler arguments
            freeCompilerArgs.addAll(
                listOf(
                    "-Xjvm-default=all",
                    "-opt-in=kotlin.RequiresOptIn",
                    "-opt-in=kotlinx.coroutines.ExperimentalCoroutinesApi",
                    "-opt-in=kotlin.ExperimentalStdlibApi"
                )
            )
        }
    }

    /**
     * Adds dependencies that ALL modules need
     */
    private fun org.gradle.api.artifacts.dsl.DependencyHandler.addUniversalDependencies(libs: VersionCatalog) {
        // Kotlin Coroutines - essential for modern Android
        add("implementation", libs.findLibrary("kotlinx-coroutines-core").get())
        add("implementation", libs.findLibrary("kotlinx-coroutines-android").get())

        // Logging - every module should be able to log
        add("implementation", libs.findLibrary("timber").get())

        // Core library desugaring for older API support
        add("coreLibraryDesugaring", libs.findLibrary("desugar-jdk-libs").get())

        // Testing - standard testing libraries for all modules
        add("testImplementation", libs.findLibrary("junit").get())
        add("androidTestImplementation", libs.findLibrary("androidx-junit").get())
        add("androidTestImplementation", libs.findLibrary("espresso-core").get())
    }
}

