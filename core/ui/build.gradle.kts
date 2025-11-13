// ═══════════════════════════════════════════════════════════════════════════
// Core UI Module - Shared UI components and Compose utilities
// ═══════════════════════════════════════════════════════════════════════════
plugins {
    id("genesis.android.library")
    alias(libs.plugins.ksp)  // Required for Hilt + Room code generation

}

android {
    namespace = "dev.aurakai.auraframefx.core.ui"
}

dependencies {
    // ═══════════════════════════════════════════════════════════════════════
    // AUTO-PROVIDED by genesis.android.library:
    // - androidx-core-ktx, appcompat
    // - Hilt (android + compiler via KSP)
    // - Timber, Coroutines
    // - Compose enabled by default
    // ═══════════════════════════════════════════════════════════════════════

    // Expose core KTX as API (types leak to consumers)
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
