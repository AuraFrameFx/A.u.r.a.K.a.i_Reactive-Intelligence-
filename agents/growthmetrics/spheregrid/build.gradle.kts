// ═══════════════════════════════════════════════════════════════════════════
// Sphere Grid Module - Agent capability grid and evolution tracking
// ═══════════════════════════════════════════════════════════════════════════
plugins {
    id("genesis.android.library")
}

android {
    namespace = "dev.aurakai.auraframefx.agents.growthmetrics.spheregrid"
}

dependencies {
    implementation(project(":agents:growthmetrics:metareflection"))
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.compose.ui)
    implementation(libs.compose.material3)
}
