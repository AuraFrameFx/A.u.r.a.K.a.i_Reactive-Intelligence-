// genesis.android.library.gradle.kts
// Convention plugin for Android library modules.
// Composes with genesis.android.base for foundational configuration.

plugins {
    // Note: Use plugin IDs directly in convention plugins, not alias()

    // 1. Apply the official Android Library plugin
    id("com.android.library")

    // 2. Apply our foundational base conventions (Kotlin, SDK versions, etc.)
    id("genesis.android.base")

    // 3. Apply KSP, as it's a common requirement for many libraries (Hilt, Room, Moshi)
    id("com.google.devtools.ksp")
}

android {
    defaultConfig {
        // Library-specific: Consumer proguard rules
        consumerProguardFiles("consumer-rules.pro")
    }

    buildTypes {
        release {
            // Libraries are typically not minified themselves
            // The app module handles minification
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
        debug {
            isMinifyEnabled = false
        }
    }
}

// Add dependencies specific to library modules
dependencies {
    // Core AndroidX - needed by most libraries
    implementation(libs.androidx.core.ktx)
}
