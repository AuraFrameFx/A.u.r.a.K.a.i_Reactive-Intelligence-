// ═══════════════════════════════════════════════════════════════════════════
// Progression Module - Agent growth and evolution tracking
// ═══════════════════════════════════════════════════════════════════════════
plugins {
    id("genesis.android.library")
}

android {
    namespace = "dev.aurakai.auraframefx.agents.growthmetrics.progression"
}

dependencies {
    implementation(project(":agents:growthmetrics:metareflection"))
    implementation(project(":agents:growthmetrics:spheregrid"))
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.compose.ui)
    implementation(libs.compose.material3)
}
