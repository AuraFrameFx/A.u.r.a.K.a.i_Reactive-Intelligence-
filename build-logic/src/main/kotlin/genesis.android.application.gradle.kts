// genesis.android.application.gradle.kts
// Convention plugin for Android application modules (typically just :app).
// Composes with genesis.android.base for foundational configuration.

plugins {
    // 1. Apply the official Android Application plugin
    alias(libs.plugins.android.application)

    // 2. Apply our foundational base conventions (Kotlin, SDK versions, etc.)
    id("genesis.android.base")

    // 3. Apply the Hilt plugin for dependency injection
    alias(libs.plugins.hilt)

    // 4. Apply KSP for code generation (used by Hilt, Room, Moshi)
    alias(libs.plugins.ksp)

    // 5. Apply the Kotlin Serialization plugin
    alias(libs.plugins.kotlin.serialization)

    // 6. Apply Compose Compiler plugin
    alias(libs.plugins.compose.compiler)

    // 7. Apply Google Services last, as recommended
    alias(libs.plugins.google.services)
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

    // ⚠️ DEPRECATED: composeOptions block no longer needed with Kotlin 2.0+
    // The built-in Compose compiler (org.jetbrains.kotlin.plugin.compose) is used automatically
    // KSP 2.3.1+ officially supports the built-in compiler
    // This block is kept ONLY for backward compatibility with this deprecated plugin
    // When using genesis.android.application (binary plugin), this is NOT needed
    // composeOptions {
    //     kotlinCompilerExtensionVersion = libs.versions.composeCompiler.get()
    // }
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
