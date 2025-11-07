package plugins

import org.gradle.api.Plugin
import org.gradle.api.Project

/**
 * Genesis Base Plugin
 * Provides base configuration for Genesis projects
 */
class GenesisBasePlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            // Base configuration for all Genesis projects
            group = "dev.aurakai.genesis"
        }
    }
}
