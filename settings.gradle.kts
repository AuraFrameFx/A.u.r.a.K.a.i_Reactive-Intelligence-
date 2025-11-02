pluginManagement {
    repositories {
        gradlePluginPortal()
        google()
        mavenCentral()
    }
}

plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "1.0.0"
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()                          // Firebase artifacts
        mavenCentral()
        maven("https://jitpack.io")       // YukiHookAPI
        maven("https://api.xposed.info/") // xposed api (safe to keep)
    }
}

    rootProject.name = "Aurakai"
// --- Application ---
    include(":app")

// --- Feature Modules ---
    include(":secure-comm")
    include(":collab-canvas")
    include(":romtools")
// Add your other feature modules here...
    include(":oracle-drive-integration")
    include(":datavein-oracle-native")
    include(":feature-module")
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
}

// Note: Do NOT include ':build-logic' here. It is handled by includeBuild.
