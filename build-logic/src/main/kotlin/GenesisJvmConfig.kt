import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.withType
import org.jetbrains.kotlin.gradle.dsl.KotlinAndroidProjectExtension
import org.jetbrains.kotlin.gradle.tasks.KotlinJvmCompile

/**
 * ===================================================================
 * GENESIS JVM CONFIGURATION
 * ===================================================================
 *
 * Centralized JVM toolchain and compilation configuration for all Genesis modules.
 *
 * This object provides:
 * - Single source of truth for JVM version across all modules
 * - Shared utility functions for configuring Kotlin JVM toolchain
 * - Consistent compiler options across all convention plugins
 *
 * @since Genesis Protocol 2.0 (AGP 9.0.0-alpha14 Compatible)
 */
object GenesisJvmConfig {
    /**
     * The JVM version used throughout the Genesis project.
     *
     * Java 24 bytecode is:
     * - Firebase compatible
     * - Maximum target supported by Kotlin 2.2.x/2.3.x
     * - Enables modern Java features with backward compatibility via desugaring
     */
    const val JVM_VERSION = 24

    /**
     * Configure the Kotlin JVM toolchain and Kotlin compilation options for the given Gradle project.
     *
     * Sets the Kotlin Android JVM toolchain to the centralized JVM_VERSION and applies compiler
     * opt-in flags for `kotlin.RequiresOptIn`, `kotlinx.coroutines.ExperimentalCoroutinesApi`, and
     * `androidx.compose.material3.ExperimentalMaterial3Api`. The JVM toolchain selection also determines
     * the effective `jvmTarget`, so manually setting `jvmTarget` is unnecessary.
     *
     * @param project The Gradle project to configure.
     */
    fun configureKotlinJvm(project: Project) {
        with(project) {
            // Configure Kotlin JVM toolchain to match Java toolchain (uses foojay-resolver)
            // This automatically sets jvmTarget, making manual jvmTarget.set() redundant
            extensions.configure<KotlinAndroidProjectExtension> {
                jvmToolchain(JVM_VERSION)
            }

            // Configure Kotlin compilation options with opt-ins
            tasks.withType<KotlinJvmCompile>().configureEach {
                compilerOptions {
                    // Note: jvmTarget is automatically set by jvmToolchain() above
                    // Manual jvmTarget.set(JvmTarget.JVM_24) is redundant
                    freeCompilerArgs.addAll(
                        "-opt-in=kotlin.RequiresOptIn",
                        "-opt-in=kotlinx.coroutines.ExperimentalCoroutinesApi",
                        "-opt-in=androidx.compose.material3.ExperimentalMaterial3Api"
                    )
                }
            }
        }
    }
}