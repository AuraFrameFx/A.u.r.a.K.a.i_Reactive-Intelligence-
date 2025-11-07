// ═══════════════════════════════════════════════════════════════════════════
// Meta Reflection Module - Agent self-analysis and introspection
// ═══════════════════════════════════════════════════════════════════════════
plugins {
    id("genesis.android.library")
}

android {
    namespace = "dev.aurakai.auraframefx.agents.growthmetrics.metareflection"
}

dependencies {
    implementation(libs.kotlinx.serialization.json)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.compose.ui)
    implementation(libs.compose.material3)
}
