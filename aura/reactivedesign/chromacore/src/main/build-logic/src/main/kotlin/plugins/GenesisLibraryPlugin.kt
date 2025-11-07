package plugins

import org.gradle.api.Plugin
import org.gradle.api.Project

/**
 * Genesis Library Plugin
 * Configures Android library modules with Genesis conventions
 */
class GenesisLibraryPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target.pluginManager) {
            apply("com.android.library")
            apply("com.google.dagger.hilt.android")
            apply("com.google.devtools.ksp")

        }
    }
}
