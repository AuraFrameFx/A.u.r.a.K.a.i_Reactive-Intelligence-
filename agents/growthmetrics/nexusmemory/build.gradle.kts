// ═══════════════════════════════════════════════════════════════════════════
// Nexus Memory Module - Agent memory and knowledge management
// ═══════════════════════════════════════════════════════════════════════════
plugins {
    id("genesis.android.library.hilt")
}

android {
    namespace = "dev.aurakai.auraframefx.agents.growthmetrics.nexusmemory"
}

dependencies {
    implementation(libs.androidx.room.runtime)
    implementation(libs.androidx.room.ktx)
    ksp(libs.androidx.room.compiler)
    implementation(libs.kotlinx.serialization.json)
}
