// ═══════════════════════════════════════════════════════════════════════════
// Data Delivery Module - Data delivery and synchronization
// ═══════════════════════════════════════════════════════════════════════════
plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.plugin.compose")
    id("com.google.devtools.ksp")
}

android {
    namespace = "dev.aurakai.auraframefx.cascade.datastream.delivery"
    compileSdk = libs.versions.compile.sdk.get().toInt()

    defaultConfig {
        minSdk = libs.versions.min.sdk.get().toInt()
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_24
        targetCompatibility = JavaVersion.VERSION_24
        isCoreLibraryDesugaringEnabled = true
    }

    buildFeatures {
        compose = true
    }
}

dependencies {
    // Hilt Dependency Injection
    implementation(libs.hilt.android)
    ksp(libs.hilt.compiler)

    implementation(project(":cascade:datastream:routing"))
    implementation(libs.okhttp)
    implementation(libs.retrofit)
    implementation(libs.kotlinx.serialization.json)

    // Core Library Desugaring (Java 24 APIs)
    coreLibraryDesugaring(libs.desugar.jdk.libs)
}
