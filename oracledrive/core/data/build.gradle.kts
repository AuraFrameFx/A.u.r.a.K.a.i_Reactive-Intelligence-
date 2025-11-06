// ═══════════════════════════════════════════════════════════════════════════
// Oracle Drive - Core Data Layer
// ═══════════════════════════════════════════════════════════════════════════
plugins {
    id("genesis.android.library")
}

android {
    namespace = "dev.aurakai.auraframefx.core.data"
}

dependencies {
    // Expose domain layer
    api(project(":oracledrive:core:domain"))

    // Common utilities
    implementation(project(":oracledrive:core:common"))

    // Data layer dependencies
    implementation(libs.androidx.datastore.preferences)
    implementation(libs.androidx.datastore.core)

    // Hilt
    implementation(libs.hilt.android)
    ksp(libs.hilt.compiler)

    // Root/System Operations
    implementation(libs.libsu.core)
    implementation(libs.libsu.io)
    implementation(libs.libsu.service)

    // Networking
    implementation(libs.okhttp)
    implementation(libs.okhttp.logging.interceptor)
    implementation(libs.retrofit.converter.kotlinx.serialization)

    // Xposed/YukiHook (compile-only)
    compileOnly(libs.xposed.api)
    compileOnly(libs.yukihookapi.api)
}
