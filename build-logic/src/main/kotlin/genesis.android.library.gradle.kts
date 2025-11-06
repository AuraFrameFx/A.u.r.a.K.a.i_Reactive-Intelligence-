// Precompiled script plugin for Android library modules
// This convention sets up a standard Android library with Kotlin

plugins {
    // 1. Apply the official Android Library plugin (replaces need for "com.android.base")
    alias(libs.plugins.android.library)

    // 2. Apply the Kotlin Android plugin
    alias(libs.plugins.kotlin.android)

    // 3. Apply KSP, as it's a common requirement for many libraries (Hilt, Room, Moshi)
    alias(libs.plugins.ksp)
}

android {
    // Common SDK versions
    compileSdk = libs.versions.compile.sdk.get().toInt()

    defaultConfig {
        minSdk = libs.versions.min.sdk.get().toInt()
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }

    // Java compatibility - Target Java 21 for modern Android
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_21
        targetCompatibility = JavaVersion.VERSION_21
        isCoreLibraryDesugaringEnabled = true
    }
}

// Configure Kotlin compiler options
tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile>().configureEach {
    compilerOptions {
        // Target Java 21 bytecode for modern Android
        jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_21)

        // Enable progressive mode
        progressiveMode.set(true)

        // Opt-in to experimental APIs
        freeCompilerArgs.addAll(
            listOf(
                "-opt-in=kotlin.RequiresOptIn",
                "-opt-in=kotlinx.coroutines.ExperimentalCoroutinesApi"
            )
        )
    }
}

// Add dependencies that nearly ALL library modules will need
dependencies {
    // Core Android
    implementation(libs.androidx.core.ktx)

    // Logging
    implementation(libs.timber)

    // Desugaring for older APIs
    coreLibraryDesugaring(libs.desugar.jdk.libs)

    // Testing
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}
