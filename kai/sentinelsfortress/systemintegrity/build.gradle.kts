// ═══════════════════════════════════════════════════════════════════════════
// System Integrity Module - System health and integrity monitoring
// ═══════════════════════════════════════════════════════════════════════════
plugins {
    id("genesis.android.library")
}

android {
    namespace = "dev.aurakai.auraframefx.kai.sentinelsfortress.systemintegrity"
}

dependencies {
    // ═══════════════════════════════════════════════════════════════════════
    // AUTO-PROVIDED by genesis.android.library:
    // - androidx-core-ktx, appcompat, timber
    // - Hilt (android + compiler via KSP)
    // - Coroutines (core + android)
    // - Compose enabled by default
    // ═══════════════════════════════════════════════════════════════════════

    implementation(libs.libsu.core)
    implementation(libs.libsu.io)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.compose.ui)
    implementation(libs.compose.material3)
}
