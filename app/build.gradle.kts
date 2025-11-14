// ═══════════════════════════════════════════════════════════════════════════
// PRIMARY CONVENTION PLUGIN - All-in-one Application Configuration
// ═══════════════════════════════════════════════════════════════════════════
// Plugins are now versioned in the root build.gradle.kts
// All plugin versions are managed centrally in the root project
plugins {
    // Core Android and Kotlin plugins
    id("com.android.application")
    id("org.jetbrains.kotlin.android")

    // Compose and serialization
    id("org.jetbrains.kotlin.plugin.compose")
    id("org.jetbrains.kotlin.plugin.serialization")

    // Dependency injection and code generation
    id("com.google.dagger.hilt.android")
    id("com.google.devtools.ksp")

    // Firebase and analytics
    id("com.google.gms.google-services")
    id("com.google.firebase.crashlytics")
}

android {
    namespace = "dev.aurakai.auraframefx"
    ndkVersion = libs.versions.ndk.get()
    compileSdk = libs.versions.compile.sdk.get().toInt()

    defaultConfig {
        applicationId = "dev.aurakai.auraframefx"
        minSdk = libs.versions.min.sdk.get().toInt()
        targetSdk = libs.versions.target.sdk.get().toInt()
        versionCode = 1
        versionName = "0.1.0"

        // Enable MultiDex (required for 276K+ methods)
        multiDexEnabled = true

        // Genesis Protocol: Gemini 2.0 Flash API Key
        // Add to local.properties: GEMINI_API_KEY=your_key_here
        // Get key from: https://aistudio.google.com/app/apikey
        val geminiApiKey = project.findProperty("GEMINI_API_KEY")?.toString() ?: ""
        buildConfigField("String", "GEMINI_API_KEY", "\"$geminiApiKey\"")

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

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_24
        targetCompatibility = JavaVersion.VERSION_24
        isCoreLibraryDesugaringEnabled = true
    }

    kotlinOptions {
        jvmTarget = "24"
        freeCompilerArgs += listOf(
            "-opt-in=kotlin.RequiresOptIn",
            "-opt-in=kotlinx.coroutines.ExperimentalCoroutinesApi",
            "-opt-in=androidx.compose.material3.ExperimentalMaterial3Api"
        )
    }

    lint {
        baseline = file("lint-baseline.xml")
        abortOnError = false
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
    // ⚠️ ONLY declare module-specific dependencies below!
    // ═══════════════════════════════════════════════════════════════════════════

    // Hilt Dependency Injection (MUST be added before afterEvaluate)
    implementation(libs.hilt.android)
    ksp(libs.hilt.compiler)

    // Core Android
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.material)
    implementation(libs.androidx.activity.compose)

    // MultiDex support for 64K+ methods
    implementation("androidx.multidex:multidex:2.0.1")

    // Compose BOM & UI
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.compose.ui)
    implementation(libs.compose.ui.graphics)
    implementation(libs.compose.ui.tooling.preview)
    implementation(libs.compose.material3)
    implementation(libs.compose.animation)
    implementation(libs.compose.material.icons.extended)
    debugImplementation(libs.compose.ui.tooling)

    // Compose Extras (Navigation, Animation)
    implementation(libs.androidx.navigation.compose)
    implementation(libs.androidx.hilt.navigation.compose)

    // Lifecycle
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.lifecycle.viewmodel.compose)
    implementation(libs.androidx.lifecycle.runtime.compose)

    // Room Database
    implementation(libs.androidx.room.runtime)
    implementation(libs.androidx.room.ktx)
    ksp(libs.androidx.room.compiler)

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
    compileOnly(libs.yukihookapi.api)
    ksp(libs.yukihookapi.ksp)

    // Firebase BOM (Bill of Materials) for version management
    implementation(platform(libs.firebase.bom))
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

    // Kotlin Serialization
    implementation(libs.kotlinx.serialization.json)

    // Moshi (JSON - for Retrofit)
    implementation(libs.moshi)
    implementation(libs.moshi.kotlin)
    ksp(libs.moshi.kotlin.codegen)

    // Kotlin DateTime & Coroutines
    implementation(libs.kotlinx.datetime)
    implementation(libs.kotlinx.coroutines.core)
    implementation(libs.kotlinx.coroutines.android)

    // Image Loading
    implementation(libs.coil.compose)
    implementation(libs.coil.svg)
    implementation(libs.coil.network.okhttp)

    // Animations
    implementation(libs.lottie.compose)

    // Logging
    implementation(libs.timber)

    // Memory Leak Detection
    debugImplementation(libs.leakcanary.android)

    // Android API JARs (Xposed)
    compileOnly(files("$projectDir/libs/api-82.jar"))
    compileOnly(files("$projectDir/libs/api-82-sources.jar"))

    // AI & ML - Google Generative AI SDK
    implementation(libs.generativeai)

    // Core Library Desugaring (Java 24 APIs)
    coreLibraryDesugaring(libs.desugar.jdk.libs)

    // ═══════════════════════════════════════════════════════════════════════════
    // Internal Project Modules - Core
    // ═══════════════════════════════════════════════════════════════════════════

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

// Force a single annotations artifact to avoid duplicate-class errors
configurations.all {
    // Skip androidTest configurations to avoid issues with local JARs
    if (name.contains("AndroidTest")) {
        return@all
    }

    resolutionStrategy {
        force("org.jetbrains:annotations:26.0.2")
    }
}
