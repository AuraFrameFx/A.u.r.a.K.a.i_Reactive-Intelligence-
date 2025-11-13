// ═══════════════════════════════════════════════════════════════════════════
// Oracle Drive Integration Module - Cloud storage integration
// ═══════════════════════════════════════════════════════════════════════════
plugins {
    id("genesis.android.library")
    id("com.google.devtools.ksp")  // Required for YukiHook annotation processing
}

android {
    namespace = "dev.aurakai.auraframefx.genesis.oracledrive"
}

dependencies {
    // ═══════════════════════════════════════════════════════════════════════
    // AUTO-PROVIDED by genesis.android.library (base - NO Hilt):
    // ✅ androidx-core-ktx, appcompat
    // ✅ Timber, Coroutines (core + android)
    // ✅ Serialization JSON
    // ✅ Compose enabled by default
    // ✅ Core library desugaring (Java 24 APIs)
    // ✅ Xposed API (compileOnly) + EzXHelper
    // ❌ NO Hilt (use genesis.android.library.hilt if DI needed)
    // ═══════════════════════════════════════════════════════════════════════

    // Expose core KTX as API
    api(libs.androidx.core.ktx)

    // Compose UI
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.compose.ui)
    implementation(libs.compose.material3)

    // Root/System Operations
    implementation(libs.libsu.core)
    implementation(libs.libsu.io)
    implementation(libs.libsu.service)

    // Xposed API (compile-only, not bundled in APK)
    compileOnly(files("$projectDir/libs/api-82.jar"))

    // YukiHook API Code Generation (Xposed framework)
    ksp(libs.yukihookapi.ksp)
}
