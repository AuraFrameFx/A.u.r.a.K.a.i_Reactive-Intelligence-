// settings.gradle.kts

pluginManagement {
    includeBuild("build-logic")
    repositories {
        gradlePluginPortal()
        google()
        mavenCentral()
        // Kotlin dev repository for beta/EAP releases
        maven { url = uri("https://maven.pkg.jetbrains.space/kotlin/p/kotlin/dev") }
        maven { url = uri("https://jitpack.io") }
    }

plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "1.0.0"
}


dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)

    repositories {
        google()
        mavenCentral()
        maven {
            url = uri("https://jitpack.io")
            metadataSources {
                artifact()
                mavenPom()
            }
        }
        maven {
            url = uri("https://dl.google.com/dl/android/maven2/")
            metadataSources {
                artifact()
                mavenPom()
                }
            }
        maven {
            url = uri("https://api.xposed.info/")
            metadataSources {
                artifact()
                mavenPom()
            }
        }

        // Dynamically add every module's libs/ directory as a file-based maven repository
        // This discovers local jars placed in module/libs (including nested modules) and registers them so artifacts like
        // de.robv.android.xposed:api and local JARs can be resolved.
        val libsDirs = rootDir.walkTopDown().filter { it.isDirectory && File(it, "libs").exists() }.map { File(it, "libs") }.toSet()
        libsDirs.forEach { libsDir ->
            maven {
                url = uri(libsDir.toURI())
                metadataSources { artifact() }
            }
        }

        // Also include the root libs folder if present (already covered above but keep for clarity)
        val rootLibs = File(rootDir, "libs")
        if (rootLibs.exists()) {
            maven { url = uri(rootLibs.toURI()); metadataSources { artifact() } }
        }
    }
}


// Human-friendly display title: A.u.r.a.K.a.i : Reactive=Intelligence
rootProject.name = "aurakai-reactive-intelligence"
// --- Application ---
include(":app")

// --- Feature Modules ---
include(":secure-comm")
include(":collab-canvas")
include(":romtools")
// Add your other feature modules here...
include(":oracle-drive-integration")
include(":datavein-oracle-native")
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
