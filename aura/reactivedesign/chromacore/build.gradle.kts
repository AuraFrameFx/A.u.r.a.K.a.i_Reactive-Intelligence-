// ═══════════════════════════════════════════════════════════════════════════
// Color Blendr Module - Color blending and theming utilities
// ═══════════════════════════════════════════════════════════════════════════
plugins {
    id("genesis.android.library.hilt")  // KSP already included in Hilt plugin
}

android {
    namespace = "dev.aurakai.auraframefx.aura.reactivedesign.chromacore"
    // Compose enabled by genesis.android.base
}

dependencies {
    // ═══════════════════════════════════════════════════════════════════════
    // AUTO-PROVIDED by genesis.android.library:
    // - androidx-core-ktx, appcompat, timber
    // - Coroutines (core + android)
    // - Compose enabled by default
    // Note: Hilt NOT included - use genesis.android.library.hilt if needed
    // ═══════════════════════════════════════════════════════════════════════

    // Expose core KTX as API
    api(libs.androidx.core.ktx)

    // Compose UI
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.compose.ui)
    implementation(libs.compose.material3)

    // Xposed API (compile-only, not bundled in APK)
    compileOnly(files("$projectDir/libs/api-82.jar"))

    // YukiHook API Code Generation (Xposed framework)
    ksp(libs.yukihookapi.ksp)
}
