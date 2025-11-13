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
     * Configures the Kotlin JVM toolchain for the given project.
     *
     * This sets up:
     * - JVM toolchain version matching the project's Java version
     * - Automatic JVM target selection (no manual jvmTarget.set() needed)
     * - Compiler opt-ins for commonly used experimental APIs
     *
     * Note: jvmToolchain() automatically sets the correct jvmTarget, so explicit
     * jvmTarget.set() calls are redundant and should be removed.
     *
     * @param project The Gradle project to configure
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
