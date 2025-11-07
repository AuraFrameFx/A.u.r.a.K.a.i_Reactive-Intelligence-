// ═══════════════════════════════════════════════════════════════════════════
// Threat Monitor Module - Security threat detection and monitoring
// ═══════════════════════════════════════════════════════════════════════════
plugins {
    id("genesis.android.library")
}

android {
    namespace = "dev.aurakai.auraframefx.kai.sentinelsfortress.threatmonitor"
}

dependencies {
    // ═══════════════════════════════════════════════════════════════════════
    // AUTO-PROVIDED by genesis.android.library:
    // - androidx-core-ktx, appcompat, timber
    // - Hilt (android + compiler via KSP)
    // - Coroutines (core + android)
    // - Compose enabled by default
    // ═══════════════════════════════════════════════════════════════════════

    implementation(project(":kai:sentinelsfortress:security"))
    implementation(libs.libsu.core)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.compose.ui)
    implementation(libs.compose.material3)
}
