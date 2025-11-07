// ═══════════════════════════════════════════════════════════════════════════
// Identity Module - Agent identity and personality management
// ═══════════════════════════════════════════════════════════════════════════
plugins {
    id("genesis.android.library")
}

android {
    namespace = "dev.aurakai.auraframefx.agents.growthmetrics.identity"
}

dependencies {
    implementation(libs.androidx.datastore.preferences)
    implementation(libs.kotlinx.serialization.json)
}
