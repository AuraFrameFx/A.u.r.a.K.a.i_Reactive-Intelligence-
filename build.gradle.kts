plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.android.library) apply false
    alias(libs.plugins.ksp) apply false
    alias(libs.plugins.hilt) apply false
    alias(libs.plugins.google.services) apply false

}

subprojects {
    // Keep Kotlin bytecode at 24 across the board
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

    // Resolve duplicate IntelliJ/JetBrains annotations by excluding the older IntelliJ artifact
    // and forcing the org.jetbrains annotations version across all configurations.
    configurations.configureEach {
        // Exclude com.intellij:annotations when possible
        try {
            exclude(group = "com.intellij", module = "annotations")
        } catch (ignored: Exception) {
            // Some configuration implementations may not allow exclude here; it's best-effort
        }

        resolutionStrategy {
            // Force the JetBrains annotations artifact (newer, standard) to avoid duplicate classes
            force("org.jetbrains:annotations:23.0.0")

            eachDependency {
                val req = this.requested
                if (req.group == "com.intellij" && req.name == "annotations") {
                    useTarget("org.jetbrains:annotations:23.0.0")
                }
            }
        }
    }
}
