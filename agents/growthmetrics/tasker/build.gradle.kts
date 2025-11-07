// ═══════════════════════════════════════════════════════════════════════════
// Tasker Module - Agent task management and execution
// ═══════════════════════════════════════════════════════════════════════════
plugins {
    id("genesis.android.library")
}

android {
    namespace = "dev.aurakai.auraframefx.agents.growthmetrics.tasker"
}

dependencies {
    implementation(project(":cascade:datastream:taskmanager"))
    implementation(libs.androidx.work.runtime.ktx)
    implementation(libs.kotlinx.serialization.json)
}
