// Precompiled script plugin for Android application modules
// This file's name "genesis.android.application" becomes the plugin ID

plugins {
    // 1. Apply the official Android Application plugin first
    alias(libs.plugins.android.application)

    // 2. Apply the Kotlin Android plugin
    alias(libs.plugins.kotlin.android)

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
    // Common SDK versions - can be overridden in app/build.gradle.kts
    compileSdk = libs.versions.compile.sdk.get().toInt()

    defaultConfig {
        minSdk = libs.versions.min.sdk.get().toInt()
        targetSdk = libs.versions.target.sdk.get().toInt()
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables.useSupportLibrary = true
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
        debug {
            isMinifyEnabled = false
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

    // Java compatibility
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_21
        targetCompatibility = JavaVersion.VERSION_21
        isCoreLibraryDesugaringEnabled = true
    }

    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

// Configure Kotlin compiler options
tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile>().configureEach {
    compilerOptions {
        // Target Java 21 bytecode for modern Android
        jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_21)

        // Enable progressive mode for stricter checks
        progressiveMode.set(true)

        // Opt-in to experimental APIs
        freeCompilerArgs.addAll(
            listOf(
                "-opt-in=kotlin.RequiresOptIn",
                "-opt-in=kotlinx.coroutines.ExperimentalCoroutinesApi",
                "-opt-in=androidx.compose.material3.ExperimentalMaterial3Api"
            )
        )
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

    // Compose BOM
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.compose.ui)
    implementation(libs.compose.material3)
    implementation(libs.compose.ui.graphics)
    implementation(libs.compose.ui.tooling.preview)
    debugImplementation(libs.compose.ui.tooling)

    // Desugaring for older APIs
    coreLibraryDesugaring(libs.desugar.jdk.libs)

    // Timber for logging
    implementation(libs.timber)

    // Firebase BoM
    implementation(platform(libs.firebase.bom))

    // Testing
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}
