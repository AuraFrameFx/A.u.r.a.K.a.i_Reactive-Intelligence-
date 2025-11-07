// ═══════════════════════════════════════════════════════════════════════════
// Aura Customization Module - UI component editing and customization
// ═══════════════════════════════════════════════════════════════════════════
plugins {
    id("genesis.android.library")
}

android {
    namespace = "dev.aurakai.auraframefx.aura.reactivedesign.customization"
}

dependencies {
    // ═══════════════════════════════════════════════════════════════════════
    // AUTO-PROVIDED by genesis.android.library:
    // - androidx-core-ktx, appcompat, timber
    // - Hilt (android + compiler via KSP)
    // - Coroutines (core + android)
    // - Compose enabled by default
    // ═══════════════════════════════════════════════════════════════════════

    implementation(project(":core:ui"))
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.compose.ui)
    implementation(libs.compose.material3)
}
