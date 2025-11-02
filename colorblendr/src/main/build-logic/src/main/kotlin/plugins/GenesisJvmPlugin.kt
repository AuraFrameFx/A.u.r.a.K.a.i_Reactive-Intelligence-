//import org.gradle.api.JavaVersion
//import org.gradle.api.Plugin
//import org.gradle.api.Project
//import org.gradle.api.plugins.JavaPluginExtension
//import org.gradle.kotlin.dsl.configure
//import org.gradle.kotlin.dsl.withType
//import org.jetbrains.kotlin.gradle.dsl.JvmTarget
//import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
//
///**
// * ===================================================================
// * GENESIS JVM CONVENTION PLUGIN
// * ===================================================================
// *
// * Convention plugin for pure Kotlin/JVM modules (non-Android).
// *
// * Plugin Application Order:
// * 1. Kotlin JVM
// * 2. Serialization
// *
// * Java Support: 25 (primary), 24 (fallback)
// *
// * @since Genesis Protocol 1.0
// */
//class GenesisJvmPlugin : Plugin<Project> {
//    override fun apply(target: Project) {
//        with(target) {            with(pluginManager) {
//                apply("org.jetbrains.kotlin.jvm")
//                apply("org.jetbrains.kotlin.plugin.serialization")
//            }
//
//                return try {
//                    JavaVersion.VERSION_25
//                } catch (e: Exception) {
//                    JavaVersion.VERSION_24
//                }
//            }
//
//                return try {
//                } catch (e: Exception) {
//            }
//        }
//    }
