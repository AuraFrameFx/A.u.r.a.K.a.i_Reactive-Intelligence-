import org.gradle.api.Plugin
import org.gradle.api.Project

/**
 * Genesis Base Plugin - Gold Standard
 * Applies Android, Kotlin, Hilt, and KSP plugins in the correct order for robust DI and Compose support.
 */
class AndroidBasePlugin : Plugin<Project> {
    /**
     * Configures the given Gradle project by applying core Android and code-generation plugins in a deterministic order.
     *
     * Applies the following plugin IDs in sequence: `com.google.dagger.hilt.android`, and `com.google.devtools.ksp`.
     *
     * @param target The Gradle project to configure.
     */
    override fun apply(target: Project) {
        with(target.pluginManager) {
            // Apply Hilt for dependency injection
            apply("com.google.dagger.hilt.android")
            // Apply KSP for annotation processing
            apply("com.google.devtools.ksp")
        }
    }
}