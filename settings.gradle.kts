import org.gradle.kotlin.dsl.project

// 1. Configure where to find PLUGINS themselves (e.g., com.android.application, kotlin-dsl).
pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}

// 2. Configure where to find all library DEPENDENCIES (e.g., androidx, retrofit).
dependencyResolutionManagement {
    // This is a best practice that forces all modules to use ONLY the repositories defined here.
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)

    repositories {
        google()
        mavenCentral()
        // Special repository for the Jetpack Compose Compiler.
        maven {
            url = uri("https://androidx.dev/storage/compose-compiler/repository/")
            name = "AndroidX Compose Compiler"
        }
    }
}

// 3. THIS IS THE CRUCIAL LINK: Tell the main build about your convention plugins.
// Gradle will now build `build-logic` first and make its plugins available to all other modules.
includeBuild("build-logic")

// 4. Define all the modules in your project.
rootProject.name = "A.u.r.a.K.a.i-ft.Genesis"

// --- Application ---
include(":app")

// --- Feature Modules ---
include(":secure-comm")
include(":collab-canvas")
include(":romtools")
// Add your other feature modules here...
include(":romtools")
include(":secure-comm")
include(":collab-canvas")
include(":datavein-oracle-native")
include(":colorblendr")
// --- Core Modules ---
include(":core:domain")
include(":core:data")
include(":core:ui")
include(":core:common")
include(":core-module")
include(":colorblendr")
include(":list")
// --- Extension Modules ---
include(":extendsysa")
include(":extendsysb")
include(":extendsysc")
include(":extendsysd")
include(":extendsyse")
include(":extendsysf")

// Note: Do NOT include ':build-logic' here. It is handled by includeBuild.
