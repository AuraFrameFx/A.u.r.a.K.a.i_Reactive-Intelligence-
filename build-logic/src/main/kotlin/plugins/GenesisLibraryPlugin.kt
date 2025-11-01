package plugins

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.internal.Actions.with

/**
 * Genesis Library Plugin
 * Configures Android library modules with Genesis conventions
 */
class GenesisLibraryPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target.pluginManager) {
            target.pluginManager
            apply("genesis.android.base")
            apply("com.android.library")
            apply("com.google.dagger.hilt.android")
            apply("com.google.devtools.ksp")
            apply("org.jetbrains.kotlin.plugin.compose")
        }
    }
}
