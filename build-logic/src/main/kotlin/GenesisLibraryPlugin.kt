import com.android.build.api.dsl.LibraryExtension
import org.gradle.api.JavaVersion
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.withType
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.tasks.KotlinJvmCompile

/**
 * ===================================================================
 * GENESIS LIBRARY CONVENTION PLUGIN
 * ===================================================================
 *
 * Convention plugin for Android library modules.
 *
 * This plugin configures:
 * - Android library plugin and extensions
 * - Kotlin Android support with Compose compiler
 * - Jetpack Compose (built-in compiler with Kotlin 2.0+)
 * - Java 24 bytecode target (Firebase compatible)
 * - Consistent build configuration across library modules
 *
 * Plugin Application Order (Critical!):
 * 1. com.android.library
 * 2. org.jetbrains.kotlin.android
 * 3. org.jetbrains.kotlin.plugin.compose (Built-in Compose compiler)
 * 4. org.jetbrains.kotlin.plugin.serialization
 *
 * Note: Hilt and KSP are NOT applied in library modules per AGP 9.0 workaround.
 * Individual library modules that need Hilt should apply it explicitly
 *
 * @since Genesis Protocol 2.0 (AGP 9.0.0-alpha14 Compatible)
 */
class GenesisLibraryPlugin : Plugin<Project> {
    /**
     * Configure the given Gradle project as an Android library module using the convention's defaults.
     *
     * Configures plugins (applied in a prescribed order), the Android LibraryExtension (compile/NDK
     * settings, defaultConfig, build types, Java/compile options, build features, packaging, and lint),
     * Kotlin compilation options (JVM 24 target and required opt-ins), and the convention's standard
     * dependencies.
     *
     * This method applies the external Kotlin Android plugin for compatibility and does not apply Hilt
     * or KSP; library modules should opt into Hilt explicitly if required.
     *
     * @param project The Gradle project to configure.
     */
    override fun apply(project: Project) {
        with(project) {
            // Apply plugins in correct order
            // Note: Using EXTERNAL kotlin-android plugin (android.builtInKotlin=false for Hilt compatibility)
            // Note: Hilt NOT applied here - library modules apply it explicitly if needed per AGP 9.0 workaround
            pluginManager.apply("com.android.library")
            pluginManager.apply("org.jetbrains.kotlin.android")
            pluginManager.apply("org.jetbrains.kotlin.plugin.compose")
            pluginManager.apply("org.jetbrains.kotlin.plugin.serialization")

            extensions.configure<LibraryExtension> {
                compileSdk = 36
                ndkVersion = "29.0.14206865"

                defaultConfig {
                    minSdk = 34
                    testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

                    ndk {
                        abiFilters += listOf("arm64-v8a", "armeabi-v7a", "x86", "x86_64")
                    }
                }

                buildTypes {
                    getByName("release") {
                        isMinifyEnabled = false
                        proguardFiles(
                            getDefaultProguardFile("proguard-android-optimize.txt"),
                            "proguard-rules.pro"
                        )
                    }
                }

                // Java 24 bytecode (Firebase compatible, Kotlin max target)
                compileOptions {
                    sourceCompatibility = JavaVersion.VERSION_24
                    targetCompatibility = JavaVersion.VERSION_24
                    isCoreLibraryDesugaringEnabled = true
                }

                // Note: kotlinOptions removed - using modern compilerOptions in tasks below

                buildFeatures {
                    compose = true
                    buildConfig = true
                    aidl = true
                }

                packaging {
                    resources {
                        excludes += setOf(
                            "/META-INF/{AL2.0,LGPL2.1}",
                            "/META-INF/LICENSE*",
                            "/META-INF/NOTICE*"
                        )
                    }
                }

                lint {
                    baseline = file("lint-baseline.xml")
                    abortOnError = false
                    checkReleaseBuilds = false
                }
            }

            // Configure Kotlin compilation with JVM 24 target (Kotlin 2.2.x/2.3.x maximum)
            tasks.withType<KotlinJvmCompile>().configureEach {
                compilerOptions {
                    jvmTarget.set(JvmTarget.JVM_24)
                    freeCompilerArgs.addAll(
                        "-opt-in=kotlin.RequiresOptIn",
                        "-opt-in=kotlinx.coroutines.ExperimentalCoroutinesApi",
                        "-opt-in=androidx.compose.material3.ExperimentalMaterial3Api"
                    )
                }
            }

            // ═══════════════════════════════════════════════════════════════════════════
            // Auto-configured dependencies (provided by convention plugin)
            // ═══════════════════════════════════════════════════════════════════════════
            // Note: Hilt dependencies REMOVED - use genesis.android.library.hilt for Hilt support

            // Core Android libraries
            dependencies.add("implementation", "androidx.core:core-ktx:1.17.0")
            dependencies.add("implementation", "androidx.appcompat:appcompat:1.7.1")

            // Kotlin Coroutines
            dependencies.add("implementation", "org.jetbrains.kotlinx:kotlinx-coroutines-core:1.10.2")
            dependencies.add("implementation", "org.jetbrains.kotlinx:kotlinx-coroutines-android:1.10.2")

            // Kotlin Serialization
            dependencies.add("implementation", "org.jetbrains.kotlinx:kotlinx-serialization-json:1.9.0")

            // Timber Logging
            dependencies.add("implementation", "com.jakewharton.timber:timber:5.0.1")

            // Core Library Desugaring (for Java 24 APIs on older Android)
            dependencies.add("coreLibraryDesugaring", "com.android.tools:desugar_jdk_libs:2.1.5")

            // Universal Xposed/LSPosed API access for all library modules
            dependencies.add("compileOnly", "de.robv.android.xposed:api:82")
            // Note: io.github.libxposed is not yet published to Maven Central
            // Use de.robv.android.xposed:api:82 for Xposed module development
            dependencies.add("implementation", "com.github.kyuubiran:EzXHelper:2.2.0")
        }
    }
}
