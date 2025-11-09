import com.android.build.api.dsl.ApplicationExtension
import org.gradle.api.JavaVersion
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.jvm.toolchain.JavaLanguageVersion
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.withType
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.tasks.KotlinJvmCompile

/**
 * ===================================================================
 * GENESIS APPLICATION CONVENTION PLUGIN
 * ===================================================================
 *
 * The primary convention plugin for Android application modules.
 *
 * This plugin configures:
 * - Android application plugin and extensions
 * - Kotlin Android support with Compose compiler
 * - Hilt dependency injection
 * - KSP annotation processing
 * - Jetpack Compose (built-in compiler with Kotlin 2.0+)
 * - Google Services (Firebase)
 * - Java 25 runtime with Java 24 bytecode target (Firebase compatible)
 * - Consistent build configuration across app modules
 *
 * Plugin Application Order (Critical!):
 * 1. com.android.application
 * 2. org.jetbrains.kotlin.android
 * 3. org.jetbrains.kotlin.plugin.compose (Built-in Compose compiler)
 * 4. com.google.dagger.hilt.android
 * 5. com.google.devtools.ksp
 * 6. org.jetbrains.kotlin.plugin.serialization
 * 7. com.google.gms.google-services
 *
 * @since Genesis Protocol 2.0 (AGP 9.0.0-alpha14 Compatible)
 */
class GenesisApplicationPlugin : Plugin<Project> {
    /**
     * Configures the given Gradle Project as an Android application module using the
     * project's standard Genesis application defaults.
     *
     * Applies required plugins (Android application, Hilt, KSP, Kotlin serialization,
     * Google services, Kotlin Android) and configures the Android ApplicationExtension:
     * compile SDK, defaultConfig (applicationId, min/target SDK, versioning, test runner,
     * vector drawable support), release build type (ProGuard/minification), Java
     * compatibility and desugaring, Compose and buildConfig build features, resource
     * packaging exclusions, lint baseline/behavior, and external native build (CMake).
     *
     * Also adjusts Kotlin compilation tasks to rely on the configured toolchain and sets
     * JVM target validation mode for Kotlin JVM compilation tasks to WARNING.
     *
     * @param project The Gradle Project to configure as an Android application module.
     */
    override fun apply(project: Project) {
        with(project) {
            // Apply plugins in correct order
            pluginManager.apply("com.android.application")
            pluginManager.apply("org.jetbrains.kotlin.android")
            pluginManager.apply("org.jetbrains.kotlin.plugin.compose")
            pluginManager.apply("com.google.dagger.hilt.android")
            pluginManager.apply("com.google.devtools.ksp")
            pluginManager.apply("org.jetbrains.kotlin.plugin.serialization")
            pluginManager.apply("com.google.gms.google-services")

            extensions.configure<ApplicationExtension> {
                compileSdk = 36
                ndkVersion = "29.0.14206865"

                defaultConfig {
                    applicationId = "dev.aurakai.auraframefx"
                    minSdk = 34
                    targetSdk = 36
                    versionCode = 1
                    versionName = "1.0"

                    testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
                    vectorDrawables {
                        useSupportLibrary = true
                    }

                    ndk {
                        abiFilters += listOf("arm64-v8a", "armeabi-v7a", "x86", "x86_64")
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

                // Java 24 bytecode (Firebase compatible) on JVM 25 runtime
                compileOptions {
                    sourceCompatibility = JavaVersion.VERSION_24
                    targetCompatibility = JavaVersion.VERSION_24
                    isCoreLibraryDesugaringEnabled = true
                }

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

                // NDK/CMake configuration (if present)
                val cmakeFile = file("src/main/cpp/CMakeLists.txt")
                if (cmakeFile.exists()) {
                    externalNativeBuild {
                        cmake {
                            path = cmakeFile
                            version = "3.22.1"
                        }
                    }
                }
            }

            // Configure Kotlin compilation with JVM 24 target
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

            // Hilt Dependency Injection
            dependencies.add("implementation", "com.google.dagger:hilt-android:2.57.2")
            dependencies.add("ksp", "com.google.dagger:hilt-android-compiler:2.57.2")

            // Compose BOM (Bill of Materials)
            dependencies.add("implementation", "platform.androidx.compose:compose-bom:2025.11.00")
            dependencies.add("implementation", "androidx.compose.ui:ui")
            dependencies.add("implementation", "androidx.compose.ui:ui-graphics")
            dependencies.add("implementation", "androidx.compose.ui:ui-tooling-preview")
            dependencies.add("implementation", "androidx.compose.material3:material3")
            dependencies.add("debugImplementation", "androidx.compose.ui:ui-tooling")

            // Core Android libraries
            dependencies.add("implementation", "androidx.core:core-ktx:1.17.0")
            dependencies.add("implementation", "androidx.appcompat:appcompat:1.7.1")
            dependencies.add("implementation", "androidx.activity:activity-compose:1.11.0")
            dependencies.add("implementation", "androidx.lifecycle:lifecycle-runtime-ktx:2.9.4")
            dependencies.add("implementation", "androidx.lifecycle:lifecycle-viewmodel-compose:2.9.4")

            // Kotlin Coroutines
            dependencies.add("implementation", "org.jetbrains.kotlinx:kotlinx-coroutines-core:1.10.2")
            dependencies.add("implementation", "org.jetbrains.kotlinx:kotlinx-coroutines-android:1.10.2")

            // Kotlin Serialization
            dependencies.add("implementation", "org.jetbrains.kotlinx:kotlinx-serialization-json:1.9.0")

            // Timber Logging
            dependencies.add("implementation", "com.jakewharton.timber:timber:5.0.1")

            // Core Library Desugaring (for Java 24 APIs on older Android)
            dependencies.add("coreLibraryDesugaring", "com.android.tools:desugar_jdk_libs:2.1.5")

            // Universal Xposed/LSPosed API access
            dependencies.add("compileOnly", "de.robv.android.xposed:api:82")
            dependencies.add("compileOnly", "io.github.libxposed:api:100")
            dependencies.add("implementation", "com.github.kyuubiran:EzXHelper:2.2.0")
        }
    }
}
