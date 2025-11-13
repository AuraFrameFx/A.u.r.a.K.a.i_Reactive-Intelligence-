// ═══════════════════════════════════════════════════════════════════════════
// PRIMARY CONVENTION PLUGIN - All-in-one Application Configuration
// ═══════════════════════════════════════════════════════════════════════════
// Plugins are now versioned in the root build.gradle.kts
// All plugin versions are managed centrally in the root project
plugins {
    // Core Android and Kotlin plugins
    id("com.android.application")

    // Compose and serialization
    alias(libs.plugins.kotlin.compose)
    id("org.jetbrains.kotlin.plugin.serialization")

    // Dependency injection and code generation
    id("com.google.dagger.hilt.android")
    id("com.google.devtools.ksp")

    // Firebase and analytics
    id("com.google.gms.google-services")
    id("com.google.firebase.crashlytics")

    // Compose tooling support

}

android {
    namespace = "dev.aurakai.auraframefx"
    ndkVersion = libs.versions.ndk.get()
        compileSdk = libs.versions.compile.sdk.get().toInt()

    defaultConfig {
        applicationId = "dev.aurakai.auraframefx"
        // minSdk, compileSdk, targetSdk are configured by genesis.android.base
        targetSdk = libs.versions.target.sdk.get().toInt()
        versionCode = 1
        versionName = "0.1.0"

        // Genesis Protocol: Gemini 2.0 Flash API Key
        // Add to local.properties: GEMINI_API_KEY=your_key_here
        // Get key from: https://aistudio.google.com/app/apikey
        val geminiApiKey = project.findProperty("GEMINI_API_KEY")?.toString() ?: ""
        buildConfigField("String", "GEMINI_API_KEY", "\"$geminiApiKey\"")

        externalNativeBuild {
            cmake {
                cppFlags += "-std=c++20"
                arguments += listOf(
                    "-DANDROID_STL=c++_shared", "-DANDROID_PLATFORM=android-${libs.versions.min.sdk.get()}"
                )
            }
        }
    }

    lint {
        baseline = file("lint-baseline.xml")
        abortOnError = true // Re-enabled: baseline suppresses known issues
        checkReleaseBuilds = false
    }

    buildFeatures {
        buildConfig = true
        compose = true
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
    // AUTO-PROVIDED by genesis.android.application:
    // ═══════════════════════════════════════════════════════════════════════════
    // ✅ Hilt Android + Compiler (with KSP)
    // ✅ Compose BOM + UI (ui, ui-graphics, ui-tooling-preview, material3, ui-tooling[debug])
    // ✅ Core Android (core-ktx, appcompat, activity-compose)
    // ✅ Lifecycle (runtime-ktx, viewmodel-compose)
    // ✅ Kotlin Coroutines (core + android)
    // ✅ Kotlin Serialization JSON
    // ✅ Timber (logging)
    // ✅ Core library desugaring (Java 24 APIs)
    // ✅ Firebase BOM
    // ✅ Xposed API (compileOnly) + EzXHelper
    //
    // ⚠️  ONLY declare module-specific dependencies below!
    // ═══════════════════════════════════════════════════════════════════════════

    // Compose Extras (Navigation, Animation - NOT in convention plugin)
    implementation(libs.compose.animation)
    implementation(libs.androidx.navigation.compose)
// ═══════════════════════════════════════════════════════════════════════════

// ═══════════════════════════════════════════════════════════════════════════

// Hilt Dependency Injection (MUST be added before afterEvaluate)
    dependencies.add("implementation", "com.google.dagger:hilt-android:2.57.2")
    dependencies.add("ksp", "com.google.dagger:hilt-android-compiler:2.57.2")
    // Material Design (legacy)
    implementation(libs.androidx.material)

    // Room Database
    implementation(libs.androidx.room.runtime)
    implementation(libs.androidx.room.ktx)

    // WorkManager
    implementation(libs.androidx.work.runtime.ktx)
    implementation(libs.androidx.hilt.work)

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

    // Firebase BOM (Bill of Materials) for version management
    implementation(platform(libs.firebase.bom))
    //using BOM do not call dependencies Gradle will read toml after confirming BOM//

    // Networking
    implementation(libs.okhttp)
    implementation(libs.okhttp.logging.interceptor)
    implementation(libs.retrofit)
    implementation(libs.retrofit.converter.kotlinx.serialization)
    implementation(libs.retrofit.converter.moshi)

    // Moshi (JSON - for Retrofit)
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

    // Android API JARs (legacy - consider removing if EzXHelper provides this)
    compileOnly(files("$projectDir/libs/api-82.jar"))
    compileOnly(files("$projectDir/libs/api-82-sources.jar"))

    // AI & ML - Google Generative AI SDK
    //same as Firebase BOM do not call from app module gradle will confirm BOM and check toml//

    // Internal Project Modules - Core

    // Aura → ReactiveDesign (Creative UI & Collaboration)
    implementation(project(":aura:reactivedesign:auraslab"))
    implementation(project(":aura:reactivedesign:collabcanvas"))
    implementation(project(":aura:reactivedesign:chromacore"))
    implementation(project(":aura:reactivedesign:customization"))

    // Kai → SentinelsFortress (Security & Threat Monitoring)
    implementation(project(":kai:sentinelsfortress:security"))
    implementation(project(":kai:sentinelsfortress:systemintegrity"))
    implementation(project(":kai:sentinelsfortress:threatmonitor"))

    // Genesis → OracleDrive (System & Root Management)
    implementation(project(":genesis:oracledrive"))
    implementation(project(":genesis:oracledrive:rootmanagement"))
    implementation(project(":genesis:oracledrive:datavein"))

    // Cascade → DataStream (Data Routing & Delivery)
    implementation(project(":cascade:datastream:routing"))
    implementation(project(":cascade:datastream:delivery"))
    implementation(project(":cascade:datastream:taskmanager"))

    // Agents → GrowthMetrics (AI Agent Evolution)
    implementation(project(":agents:growthmetrics:metareflection"))
    implementation(project(":agents:growthmetrics:nexusmemory"))
    implementation(project(":agents:growthmetrics:spheregrid"))
    implementation(project(":agents:growthmetrics:identity"))
    implementation(project(":agents:growthmetrics:progression"))
    implementation(project(":agents:growthmetrics:tasker"))

}

