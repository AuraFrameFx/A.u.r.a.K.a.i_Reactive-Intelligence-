// ═══════════════════════════════════════════════════════════════════════════
// PRIMARY CONVENTION PLUGIN - All-in-one Application Configuration
// ═══════════════════════════════════════════════════════════════════════════
// This single plugin applies (in correct order):
// 1. com.android.application
// 2. com.google.dagger.hilt.android (Dependency Injection)
// 3. com.google.devtools.ksp (Annotation Processing)
// 4. org.jetbrains.kotlin.plugin.compose (Compose Compiler)
// 5. org.jetbrains.kotlin.plugin.serialization (JSON serialization)
// 6. com.google.gms.google-services (Firebase)
// 7. genesis.android.base (SDK config, universal dependencies)
//
// NO NEED to declare plugins individually with version numbers anymore!
// ═══════════════════════════════════════════════════════════════════════════
plugins {
    id("genesis.android.application")
    id("org.jetbrains.kotlin.plugin.serialization") version "2.2.21"
    id("com.google.dagger.hilt.android") version libs.versions.hilt.get()
    id("com.google.devtools.ksp") version libs.versions.ksp.get()
    id("org.jetbrains.kotlin.plugin.compose") version libs.versions.kotlin.get()
    id("com.google.gms.google-services") version libs.versions.googleServices.get()
    id("genesis.android.base") version libs.versions.genesis.android.base.get()

}

android {
    namespace = "dev.aurakai.auraframefx"
    ndkVersion = libs.versions.ndk.get()

    defaultConfig {
        applicationId = "dev.aurakai.auraframefx"
        // minSdk, compileSdk, targetSdk are configured by genesis.android.base
        targetSdk = libs.versions.target.sdk.get().toInt()
        versionCode = 1
        versionName = "0.1.0"

        externalNativeBuild {
            cmake {
                cppFlags += "-std=c++20"
                arguments += listOf(
                    "-DANDROID_STL=c++_shared",
                    "-DANDROID_PLATFORM=android-${libs.versions.min.sdk.get()}"
                )
            }
        }
    }

    lint {
        baseline = file("lint-baseline.xml")
        abortOnError = true
        checkReleaseBuilds = false
    }

    externalNativeBuild {
        cmake {
            path = file("src/main/cpp/CMakeLists.txt")
            version = libs.versions.cmake.get()
        }
    }
}
dependencies {
    // ═══════════════════════════════════════════════════════════════════════════
    // NOTE: The following are AUTOMATICALLY provided by genesis.android.application:
    // - Kotlin Coroutines (core + android)
    // - Timber (logging)
    // - Testing libraries (JUnit, AndroidX JUnit, Espresso)
    // - Core library desugaring
    // - Hilt Android + Compiler (auto-wired with KSP)
    //
    // You only need to declare module-specific dependencies below!
    // ═══════════════════════════════════════════════════════════════════════════

    // Compose UI
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.compose.ui)
    implementation(libs.compose.ui.graphics)
    implementation(libs.compose.ui.tooling.preview)
    implementation(libs.compose.material3)
    implementation(libs.compose.animation)
    debugImplementation(libs.compose.ui.tooling)

    // AndroidX Core
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.material)
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.navigation.compose)

    // Lifecycle Components
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.lifecycle.viewmodel.ktx)
    implementation(libs.androidx.lifecycle.viewmodel.compose)

    // Room Database
    implementation(libs.androidx.room.runtime)
    implementation(libs.androidx.room.ktx)
    ksp(libs.androidx.room.compiler)

    // WorkManager
    implementation(libs.androidx.work.runtime.ktx)
    implementation(libs.androidx.hilt.work)
    ksp(libs.androidx.hilt.compiler)

    // DataStore
    implementation(libs.androidx.datastore.preferences)
    implementation(libs.androidx.datastore.core)

    // Security
    implementation(libs.androidx.security.crypto)

    // Root/System Utils
    implementation(libs.libsu.core)
    implementation(libs.libsu.io)
    implementation(libs.libsu.service)

    // YukiHook API
    ksp(libs.yukihookapi.api)

    // Firebase
    implementation(libs.firebase.analytics)
    implementation(libs.firebase.crashlytics)
    implementation(libs.firebase.auth)
    implementation(libs.firebase.firestore)

    // Networking
    implementation(libs.okhttp)
    implementation(libs.okhttp.logging.interceptor)
    implementation(libs.retrofit)
    implementation(libs.retrofit.converter.kotlinx.serialization)
    implementation(libs.retrofit.converter.moshi)

    // Serialization
    implementation(libs.kotlinx.serialization.json)
    implementation(libs.moshi)
    implementation(libs.moshi.kotlin)
    ksp(libs.moshi.kotlin.codegen)

    // Kotlin DateTime
    implementation(libs.kotlinx.datetime)

    // Image Loading
    implementation(libs.coil.compose)
    implementation(libs.coil.svg)

    // Animations
    implementation(libs.lottie.compose)

    // Memory Leak Detection
    debugImplementation(libs.leakcanary.android)

    // Android API JARs
    compileOnly(files("$projectDir/libs/api-82.jar"))
    compileOnly(files("$projectDir/libs/api-82-sources.jar"))

    // Internal Project Modules
    implementation(project(":core-module"))
    implementation(project(":core:common"))
    implementation(project(":core:domain"))
    implementation(project(":core:data"))
    implementation(project(":core:ui"))
    implementation(project(":feature-module"))
    implementation(project(":secure-comm"))
    implementation(project(":romtools"))
    implementation(project(":colorblendr"))
    implementation(project(":list"))
    implementation(project(":oracle-drive-integration"))
    implementation(project(":collab-canvas"))
    implementation(project(":datavein-oracle-native"))
}
