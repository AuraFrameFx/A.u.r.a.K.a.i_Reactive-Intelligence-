package plugins

import org.gradle.api.Plugin
import org.gradle.api.Project

/**
 * Android library conventions. AGP9 alpha builds sometimes need com.android.base present.
 */
class GenesisLibraryPlugin : Plugin<Project> {
    override fun apply(target: Project) = with(target.pluginManager) {
        apply("com.android.base")
        apply("genesis.android.library")
        // helps certain plugin checks in AGP 9 alphas
        apply("org.jetbrains.kotlin.android")
        apply("com.google.dagger.hilt.android")
        apply("com.google.devtools.ksp")
        apply("org.jetbrains.kotlin.plugin.compose")
        apply("org.jetbrains.kotlin.plugin.serialization")
    }
}
plugins.withId("org.jetbrains.kotlin.android") {
    extensions.configure<org.jetbrains.kotlin.gradle.dsl.KotlinAndroidProjectExtension> {
        compilerOptions.jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.fromTarget("24"))
    }
}
plugins.withId("org.jetbrains.kotlin.jvm") {
    extensions.configure<org.jetbrains.kotlin.gradle.dsl.KotlinJvmProjectExtension> {
        compilerOptions.jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.fromTarget("24"))
    }
}
buildFeatures { compose = true }
// You might also need a composeOptions block
composeOptions {
    kotlinCompilerExtensionVersion = libs.versions.compose.compiler.get()
}
