// ═══════════════════════════════════════════════════════════════════════════
// PRIMARY CONVENTION PLUGIN - All-in-one Application Configuration
// ═══════════════════════════════════════════════════════════════════════════
// This single plugin applies (in correct order):
// 1. com.android.application
// 2. com.google.dagger.hilt.android (Dependency Injection)
// 3. com.google.devtools.ksp (Annotation Processing)
// 4. org.jetbrains.kotlin.plugin.compose (Compose Compiler)
// 5. genesis.android.base (SDK config, universal dependencies)
//
// NO NEED to declare plugins individually - GenesisApplicationPlugin handles everything!
// ═══════════════════════════════════════════════════════════════════════════
plugins {
    id("genesis.android.application")
    id("com.google.devtools.ksp")
    id("com.google.dagger.hilt.android")
    id("org.jetbrains.kotlin.android")
    id("org.jetbrains.kotlin.plugin.serialization")
    id("com.google.gms.google-services")
    id("com.google.firebase.crashlytics")
    // NOTE: Firebase Analytics is NOT a plugin - it's automatically included via Firebase BOM
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


                lint {
                    baseline = file("lint-baseline.xml")
                    abortOnError = true
                    checkReleaseBuilds = false
                }

                buildFeatures {
                    buildConfig = true
                    compose = true
                }
                externalNativeBuild {
                    cmake {
                        var path = file("src/main/cpp/CMakeLists.txt")
                        version = libs.versions.cmake.get()
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
                    implementation(libs.androidx.lifecycle.viewmodel.compose)

                    // Room Database
                    implementation(libs.androidx.room.runtime)
                    implementation(libs.androidx.room.ktx)

                    // Hilt Dependency Injection (REQUIRED when using Hilt plugin)
                    implementation(libs.hilt.android)
                    ksp(libs.hilt.compiler)

                    // WorkManager
                    implementation(libs.androidx.work.runtime.ktx)
                    implementation(libs.androidx.hilt.work)
                    ksp(libs.hilt.compiler)

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

                    // Kotlin + utils
                    implementation(libs.kotlinx.coroutines.core)
                    implementation(libs.kotlinx.coroutines.android)
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
            }
        }
    }
}
