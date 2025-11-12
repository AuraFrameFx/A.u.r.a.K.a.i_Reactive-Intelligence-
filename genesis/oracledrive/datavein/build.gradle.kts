// ═══════════════════════════════════════════════════════════════════════════
// DataVein Oracle Native Module - Native Oracle cloud data access
// ═══════════════════════════════════════════════════════════════════════════
plugins {
    id("genesis.android.library")
    id("com.google.devtools.ksp")  // Required for YukiHook annotation processing
}

android {
    namespace = "dev.aurakai.auraframefx.genesis.oracledrive.datavein"
}

dependencies {
    // ═══════════════════════════════════════════════════════════════════════
    // AUTO-PROVIDED by genesis.android.library:
    // - androidx-core-ktx, appcompat, timber
    // - Coroutines (core + android)
    // - Compose enabled by default
    // 
    // NOTE: Hilt is NOT auto-provided by genesis.android.library
    // Use genesis.android.library.hilt if Hilt dependency injection is needed
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
