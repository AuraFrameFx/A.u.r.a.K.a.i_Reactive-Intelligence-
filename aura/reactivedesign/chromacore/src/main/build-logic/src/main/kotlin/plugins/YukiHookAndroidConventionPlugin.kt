import com.android.build.api.dsl.ApplicationExtension
import com.android.build.api.dsl.LibraryExtension
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure

class YukiHookAndroidConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            // Configure Android applications for Yuki Hook
            pluginManager.withPlugin("com.android.application") {
                extensions.configure<ApplicationExtension> {
                    configureAndroidApplication(this)
                }
            }


            // Configure Android libraries for Yuki Hook
            pluginManager.withPlugin("com.android.library") {
            }
        }
    }
}

/**
 * Apply Android application defaults required by the Yuki Hook integration.
 *
 * Configures compile SDK, defaultConfig (minSdk, targetSdk, and instrumentation runner),
 * the release build type (minification and ProGuard files), and Java compile options.
 *
 * @param extension The Android ApplicationExtension to configure.
 */
private fun configureAndroidApplication(extension: ApplicationExtension) {
    extension.apply {
        compileSdk = 36

        defaultConfig {
            minSdk = 34
            targetSdk = 36
            testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        }

        buildTypes {
            getByName("release") {
                isMinifyEnabled = true
                proguardFiles(
                    getDefaultProguardFile("proguard-android-optimize.txt"),
                    "proguard-rules.pro"
                )
            }
        }

    }

    /**
     * Configure the Android LibraryExtension with Yuki Hook's required build defaults.
     *
     * Configures compileSdk, defaultConfig (minSdk, test instrumentation runner, consumer ProGuard rules),
     * and the release build type's minification and ProGuard files.
     */
    fun configureAndroidLibrary(extension: LibraryExtension) {
        extension.apply {
            compileSdk = 36

            defaultConfig {
                minSdk = 34
                testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
                consumerProguardFiles("consumer-rules.pro")
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
        }
    }
}