package plugins

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.dsl.KotlinAndroidProjectExtension
import org.jetbrains.kotlin.gradle.dsl.KotlinJvmProjectExtension

class GenesisBasePlugin : Plugin<Project> {
    override fun apply(target: Project) = with(target) {
        // Centralize Kotlin bytecode target = 24 (run JDK 25 toolchains)
        plugins.withId("org.jetbrains.kotlin.android") {
            extensions.configure(KotlinAndroidProjectExtension::class.java) {
                compilerOptions.jvmTarget.set(JvmTarget.fromTarget("24"))
            }
        }
        plugins.withId("org.jetbrains.kotlin.jvm") {
            extensions.configure(KotlinJvmProjectExtension::class.java) {
                compilerOptions.jvmTarget.set(JvmTarget.fromTarget("24"))
            }
        }
    }
}
