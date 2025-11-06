// genesis.android.base.gradle.kts
// The foundational layer for ALL Android modules.
// Establishes baseline configuration: SDK versions, Java/Kotlin compatibility, universal dependencies.

import com.android.build.api.dsl.CommonExtension

plugins {
    // Note: Use kotlin() DSL or plugin ID directly in convention plugins, not alias()
    // Kotlin Android is universal across all Android modules
    kotlin("android")
}

// Configure the 'android' block for both app and library modules
// CommonExtension is the shared parent of ApplicationExtension and LibraryExtension
extensions.configure<CommonExtension<*, *, *, *, *, *>> {
    compileSdk = libs.versions.compile.sdk.get().toInt()

    defaultConfig {
        minSdk = libs.versions.min.sdk.get().toInt()
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    // CRITICAL: Java/Kotlin Bytecode Target
    // While the JDK toolchain is 21, the output bytecode MUST be compatible with
    // the Android Runtime (ART). We target Java 17 for broad device compatibility.
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
        isCoreLibraryDesugaringEnabled = true
    }

    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
            // Exclude duplicate files that cause build errors
            excludes += "/META-INF/LICENSE.md"
            excludes += "/META-INF/LICENSE-notice.md"
        }
    }
}

// Configure Kotlin compiler options for all Android modules
tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile>().configureEach {
    compilerOptions {
        // Align with compileOptions targetCompatibility
        jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_17)

        // Enable progressive mode for stricter checks
        progressiveMode.set(true)

        // Compiler arguments
        freeCompilerArgs.addAll(
            listOf(
                "-Xjvm-default=all",
                "-opt-in=kotlin.RequiresOptIn",
                "-opt-in=kotlinx.coroutines.ExperimentalCoroutinesApi"
            )
        )
    }
}

// Add dependencies that are universal to ALL Android modules
dependencies {
    // Kotlin Coroutines - essential for modern Android development
    implementation(libs.kotlinx.coroutines.core)
    implementation(libs.kotlinx.coroutines.android)

    // Logging - every module should be able to log
    implementation(libs.timber)

    // Desugaring for older API support
    coreLibraryDesugaring(libs.desugar.jdk.libs)

    // Testing - standard testing libraries for all modules
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}
