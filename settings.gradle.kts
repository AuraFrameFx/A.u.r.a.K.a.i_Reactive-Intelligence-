// settings.gradle.kts

pluginManagement {
    includeBuild("build-logic")
    repositories {
        gradlePluginPortal()
        google()
        mavenCentral()
        maven { url = uri("https://jitpack.io") }
    }
    plugins {
        id("com.android.application") version "9.0.0-alpha14" apply false
        id("com.android.library") version "9.0.0-alpha14" apply false
        id("org.jetbrains.kotlin.android") version "2.3.0-Beta2" apply false
        id("org.jetbrains.kotlin.plugin.compose") version "2.3.0-Beta2" apply false
        id("org.jetbrains.kotlin.plugin.serialization") version "2.3.0-Beta2" apply false
        id("com.google.devtools.ksp") version "2.3.2" apply false
        id("com.google.dagger.hilt.android") version "2.57.2" apply false
        id("com.google.gms.google-services") version "4.4.4" apply false
        id("com.google.firebase.crashlytics") version "3.0.6" apply false
    }
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
            val libsDirs =
                rootDir.walkTopDown().filter { it.isDirectory && File(it, "libs").exists() }.map { File(it, "libs") }
                    .toSet()
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
    rootProject.name = "AuraKai"
// --- Application ---
include(":app")
//Aura / ReactiveDesign
include(":aura:reactivedesign:auraslab")
include(":aura:reactivedesign:collabcanvas")
include(":aura:reactivedesign:chromacore")
include(":aura:reactivedesign:customization")
//include(":aura:reactivedesign:workbench3d") // only if gate is ON

//kai / SentinelsFortress
include(":kai:sentinelsfortress:security")
include(":kai:sentinelsfortress:systemintegrity")
include(":kai:sentinelsfortress:threatmonitor")

//Genesis / OracleDrive
include(":genesis:oracledrive")
include(":genesis:oracledrive:rootmanagement")
include(":genesis:oracledrive:datavein")

//Cascade / DataStream
include(":cascade:datastream:routing")
include(":cascade:datastream:delivery")
include(":cascade:datastream:taskmanager")

//Agents / GrowthMetrics
include(":agents:growthmetrics:metareflection")
include(":agents:growthmetrics:nexusmemory")
include(":agents:growthmetrics:spheregrid")
include(":agents:growthmetrics:identity")
include(":agents:growthmetrics:progression")
include(":agents:growthmetrics:tasker")
// Note: Do NOT include ':build-logic' here. It is handled by includeBuild.
