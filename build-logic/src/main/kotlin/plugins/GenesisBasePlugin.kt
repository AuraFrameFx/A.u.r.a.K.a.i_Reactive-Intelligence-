package plugins

import com.android.build.api.dsl.ApplicationExtension
import com.android.build.api.dsl.LibraryExtension
import org.gradle.api.JavaVersion
import org.gradle.api.Plugin
import org.gradle.api.Project

/**
 * ═══════════════════════════════════════════════════════════════════════════
 * GENESIS BASE CONVENTION PLUGIN
 * ═══════════════════════════════════════════════════════════════════════════
 *
 * Foundational Android configuration applied to both application and library modules.
 * Sets SDK versions, Java compatibility, and enables Compose.
 *
 * Applied automatically by GenesisApplicationPlugin and GenesisLibraryPlugin.
 * Do NOT apply directly in modules!
 */
class GenesisBasePlugin : Plugin<Project> {
    override fun apply(project: Project) = with(project) {
        // Configure for application modules
        plugins.withId("com.android.application") {
            extensions.getByType(ApplicationExtension::class.java).apply {
                compileSdk = 36
                defaultConfig {
                    minSdk = 33
                }
                compileOptions {
                    sourceCompatibility = JavaVersion.VERSION_21
                    targetCompatibility = JavaVersion.VERSION_21
                }
                buildFeatures {
                    compose = true
                }
            }
        }

        // Configure for library modules
        plugins.withId("com.android.library") {
            extensions.getByType(LibraryExtension::class.java).apply {
                compileSdk = 36
                defaultConfig {
                    minSdk = 33
                }
                compileOptions {
                    sourceCompatibility = JavaVersion.VERSION_21
                    targetCompatibility = JavaVersion.VERSION_21
                }
                buildFeatures {
                    compose = true
                }
            }
        }
    }
}
