// ═══════════════════════════════════════════════════════════════════════════
// ROM Tools Module - System and ROM modification utilities
// ═══════════════════════════════════════════════════════════════════════════
plugins {
    id("genesis.android.library.hilt")  // Includes Hilt, KSP, and all base features
}

android {
    namespace = "dev.aurakai.auraframefx.genesis.oracledrive.rootmanagement"
    // Java 24 compileOptions are set by genesis.android.base

    // Disable androidTest to avoid dependency resolution issues with local JARs
    sourceSets {
        getByName("androidTest") {
            java.setSrcDirs(emptyList<String>())
            kotlin.setSrcDirs(emptyList<String>())
        }
    }

    testOptions {
        unitTests.isIncludeAndroidResources = false
    }
}

dependencies {
    // ═══════════════════════════════════════════════════════════════════════
    // AUTO-PROVIDED by genesis.android.library.hilt:
    // - androidx-core-ktx, appcompat, timber
    // - Hilt (android + compiler via KSP)
    // - KSP annotation processing
    // - Coroutines (core + android)
    // - Compose enabled by default
    // - Java 24 bytecode target
    // - KSP plugin for annotation processing
    // ═══════════════════════════════════════════════════════════════════════

    // Expose core KTX as API
    api(libs.androidx.core.ktx)

    // Compose UI
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.compose.ui)
    implementation(libs.compose.material3)
    implementation(libs.compose.material.icons.extended)
    implementation(libs.compose.ui.tooling.preview)
    debugImplementation(libs.compose.ui.tooling)

    // Compose / Lifecycle / Navigation / Hilt integrations
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.navigation.compose)
    implementation(libs.androidx.hilt.navigation.compose)
    implementation(libs.androidx.lifecycle.viewmodel.compose)
    implementation(libs.androidx.lifecycle.runtime.compose)

    // WorkManager
    implementation(libs.androidx.work.runtime.ktx)

    // Kotlin Serialization
    implementation(libs.kotlinx.serialization.json)

    // Root/System Operations
    implementation(libs.libsu.core)
    implementation(libs.libsu.io)
    implementation(libs.libsu.service)

    // Xposed API (compile-only, not bundled in APK)
    compileOnly(files("$projectDir/libs/api-82.jar"))

    // YukiHook API Code Generation (Xposed framework)
    ksp(libs.yukihookapi.ksp)
}

// Force a single annotations artifact to avoid duplicate-class errors
// Updated to 26.0.2-1 to match project dependencies
configurations.all {
    // Skip androidTest configurations to avoid issues with local JARs
    if (name.contains("AndroidTest")) {
        return@all
    }

    resolutionStrategy {
        force("org.jetbrains:annotations:26.0.2-1")
    }
}
