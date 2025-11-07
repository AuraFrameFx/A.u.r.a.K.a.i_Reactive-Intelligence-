// ═══════════════════════════════════════════════════════════════════════════
// Task Manager Module - Background task scheduling and execution
// ═══════════════════════════════════════════════════════════════════════════
plugins {
    id("genesis.android.library")
}

android {
    namespace = "dev.aurakai.auraframefx.cascade.datastream.taskmanager"
}

dependencies {
    implementation(project(":cascade:datastream:routing"))
    implementation(libs.androidx.work.runtime.ktx)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.compose.ui)
    implementation(libs.compose.material3)
}
