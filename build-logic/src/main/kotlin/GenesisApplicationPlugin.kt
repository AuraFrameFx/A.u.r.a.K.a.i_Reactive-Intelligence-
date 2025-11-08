import com.android.build.api.dsl.ApplicationExtension
import org.gradle.api.JavaVersion
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure
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
    override fun apply(project: Project) {
        with(project) {
            pluginManager.apply("com.android.application")
            pluginManager.apply("com.google.dagger.hilt.android")
            pluginManager.apply("com.google.devtools.ksp")
            pluginManager.apply("org.jetbrains.kotlin.plugin.serialization")
            pluginManager.apply("com.google.gms.google-services")
            pluginManager.apply("org.jetbrains.kotlin.android")

            extensions.configure<ApplicationExtension> {
                compileSdk = 36
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
                }

                buildTypes {
                    release {
                        isMinifyEnabled = true
                        proguardFiles(
                            getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro"
                        )
                    }
                }

                compileOptions {
                    sourceCompatibility = JavaVersion.toVersion("25")
                    targetCompatibility = JavaVersion.toVersion("25")

                    isCoreLibraryDesugaringEnabled = true
                }

                buildFeatures {
                    compose = true
                    buildConfig = true
                }

                packaging {
                    resources {
                        excludes += "/META-INF/{AL2.0,LGPL2.1}"
                    }
                }

                lint {
                    baseline = file("lint-baseline.xml")
                    abortOnError = true
                    checkReleaseBuilds = false
                }

                externalNativeBuild {
                    cmake {
                        path = file("src/main/cpp/CMakeLists.txt")
                        version = "4.1.2"
                    }
                }
            }
            tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile>().configureEach {
                // No need for specific configuration here anymore as it's handled by the toolchain
                // and the kotlinOptions block in the android extension.
            }
            tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinJvmCompile>().configureEach {
                jvmTargetValidationMode.set(org.jetbrains.kotlin.gradle.dsl.jvm.JvmTargetValidationMode.WARNING)
            }
        }
    }
}
