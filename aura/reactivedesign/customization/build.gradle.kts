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
    // - Compose BOM + core UI libraries (ui, graphics, material3)
    // - All Xposed APIs (compileOnly)
    // ═══════════════════════════════════════════════════════════════════════

    // Compose UI - BOM already provided, just declare components
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.compose.ui)
    implementation(libs.compose.material3)
}
