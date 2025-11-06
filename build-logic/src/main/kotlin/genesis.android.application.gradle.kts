// genesis.android.application.gradle.kts
// Convention plugin for Android application modules (typically just :app).
// Composes with genesis.android.base for foundational configuration.

plugins {
    // Note: Use plugin IDs directly in convention plugins, not alias()

    // 1. Apply the official Android Application plugin
    id("com.android.application")

    // 2. Apply our foundational base conventions (Kotlin, SDK versions, etc.)
    id("genesis.android.base")

    // 3. Apply the Hilt plugin for dependency injection
    id("com.google.dagger.hilt.android")

    // 4. Apply KSP for code generation (used by Hilt, Room, Moshi)
    id("com.google.devtools.ksp")

    // 5. Apply the Kotlin Serialization plugin using kotlin() DSL
    kotlin("plugin.serialization")

    // 6. Apply Compose Compiler plugin
    id("org.jetbrains.kotlin.plugin.compose")

    // 7. Apply Google Services last, as recommended
    id("com.google.gms.google-services")
}

android {
    defaultConfig {
        // App-specific: target SDK
        targetSdk = libs.versions.target.sdk.get().toInt()

        // Enable vector drawables support
        vectorDrawables.useSupportLibrary = true
    }

    buildTypes {
        release {
            // Enable code shrinking and resource shrinking for release builds
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
        debug {
            // Disable minification for faster debug builds
            isMinifyEnabled = false

            // Enable debugging
            isDebuggable = true
        }
    }

    // Enable Jetpack Compose support
    buildFeatures {
        compose = true
        buildConfig = true
    }

    // Configure the Compose compiler
    composeOptions {
        kotlinCompilerExtensionVersion = libs.versions.composeCompiler.get()
    }
}

// Add dependencies that every application module needs
dependencies {
    // Hilt
    implementation(libs.hilt.android)
    ksp(libs.hilt.compiler)

    // Core Android
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)

    // Compose BOM - manages all Compose library versions
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.compose.ui)
    implementation(libs.compose.material3)
    implementation(libs.compose.ui.graphics)
    implementation(libs.compose.ui.tooling.preview)
    debugImplementation(libs.compose.ui.tooling)

    // Firebase BoM - manages all Firebase library versions
    implementation(platform(libs.firebase.bom))

    // Serialization
    implementation(libs.kotlinx.serialization.json)
}
