// ═══════════════════════════════════════════════════════════════════════════
// Data Delivery Module - Data delivery and synchronization
// ═══════════════════════════════════════════════════════════════════════════
plugins {
    id("genesis.android.library")
}

android {
    namespace = "dev.aurakai.auraframefx.cascade.datastream.delivery"
}

dependencies {
    implementation(project(":cascade:datastream:routing"))
    implementation(libs.okhttp)
    implementation(libs.retrofit)
    implementation(libs.kotlinx.serialization.json)
}
