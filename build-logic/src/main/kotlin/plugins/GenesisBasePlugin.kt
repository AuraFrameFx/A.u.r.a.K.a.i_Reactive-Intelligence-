package plugins

import com.android.build.api.dsl.CommonExtension
import org.gradle.api.JavaVersion
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

        // Configure Android compileOptions for library and application modules
        plugins.withId("com.android.library") {
            extensions.configure(CommonExtension::class.java) {
                compileOptions {
                    sourceCompatibility = JavaVersion.VERSION_24
                    targetCompatibility = JavaVersion.VERSION_24
                }
            }
        }
        plugins.withId("com.android.application") {
            extensions.configure(CommonExtension::class.java) {
                compileOptions {
                    sourceCompatibility = JavaVersion.VERSION_24
                    targetCompatibility = JavaVersion.VERSION_24
                }
            }
        }
    }
}
