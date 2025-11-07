// ═══════════════════════════════════════════════════════════════════════════
// Data Routing Module - Data flow routing and management
// ═══════════════════════════════════════════════════════════════════════════
plugins {
    id("genesis.android.library")
}

android {
    namespace = "dev.aurakai.auraframefx.cascade.datastream.routing"
}

dependencies {
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.compose.ui)
    implementation(libs.compose.material3)
    implementation(libs.kotlinx.serialization.json)
}
