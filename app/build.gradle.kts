plugins {
    id("com.android.application") version "9.0.0-alpha13"
    id("com.google.dagger.hilt.android") version "2.57.2"
    id("com.google.devtools.ksp") version "2.3.0"

    id("org.jetbrains.kotlin.plugin.serialization") version "2.2.21"
    id("org.jetbrains.kotlin.plugin.compose") version "2.2.21"

}

android {
    namespace = "dev.aurakai.auraframefx"
    compileSdk = libs.versions.compile.sdk.get().toInt()
    defaultConfig {
        applicationId = "dev.aurakai.auraframefx"
        minSdk = libs.versions.min.sdk.get().toInt()
        targetSdk = libs.versions.target.sdk.get().toInt()
        versionCode = 1
        versionName = "0.1.0"
    }
}
dependencies {
// Compose
    // Hooking & reflection libraries — use version-catalog aliases where possible
    compileOnly(libs.xposed.api)
    implementation(libs.yukihookapi.api)
    implementation(libs.kavaref.core)
    implementation(libs.kavaref.extension)
    // If using YukiHook ksp-xposed processor (only for Xposed module usage)
    ksp(libs.yukihookapi.ksp.xposed)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.compose.ui)
    implementation(libs.compose.material3)
    implementation(libs.compose.animation)
    debugImplementation(libs.compose.ui.tooling)
    debugImplementation(libs.compose.ui.tooling.preview)
// AndroidX core
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.material)
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.navigation.compose)
    // Use KSP for Room compiler (KSP plugin is applied at the top of this build script)
    ksp(libs.androidx.room.compiler)
// Hilt (KSP line is critical)
    implementation(libs.hilt.android)
    ksp(libs.hilt.compiler)

// Root/system utils
    implementation(libs.libsu.core)
    implementation(libs.libsu.io)
    implementation(libs.libsu.service)
    implementation(libs.compose.ui.graphics) // use version-catalog entry


    implementation(libs.androidx.work.runtime.ktx)
    implementation(libs.androidx.hilt.navigation.compose)
    implementation(libs.androidx.hilt.work)
    implementation(libs.androidx.navigation.compose)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.lifecycle.viewmodel.ktx)
    implementation(libs.androidx.lifecycle.viewmodel.compose)
    implementation(libs.androidx.lifecycle.runtime.compose)
    implementation(libs.androidx.datastore.preferences)
    implementation(libs.androidx.datastore.core)
    implementation(libs.androidx.security.crypto)
    coreLibraryDesugaring(libs.desugar.jdk.libs)
    implementation(libs.leakcanary.android)


    // Kotlin datetime
    implementation(libs.kotlinx.datetime)

    // Firebase dependencies
    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.auth)
    implementation(libs.firebase.firestore)

// Networking (pick one converter path; here kotlinx‑serialization)
    implementation(libs.okhttp)
    implementation(libs.okhttp.logging.interceptor)
    implementation(libs.retrofit)
    implementation(libs.retrofit.converter.kotlinx.serialization)

// Kotlin + utils
    implementation(libs.kotlinx.coroutines.core)
    implementation(libs.kotlinx.coroutines.android)
    implementation(libs.kotlinx.serialization.json)
    implementation(libs.timber)
    implementation(libs.coil.compose)
    implementation(libs.coil.svg)
    implementation(libs.lottie.compose)

// Internal project modules - ensure app has access to shared code and generated types
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
    implementation(project(":romtools"))

}
