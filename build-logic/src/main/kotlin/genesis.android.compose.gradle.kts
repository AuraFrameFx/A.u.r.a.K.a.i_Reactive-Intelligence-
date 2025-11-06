// genesis.android.compose.gradle.kts
// Composable plugin for Jetpack Compose.
// Apply this to any library module that contains Composables.
// (The app module already includes Compose via genesis.android.application)

plugins {
    // Apply Compose Compiler plugin
    // Note: Use plugin ID directly in convention plugins, not alias()
    id("org.jetbrains.kotlin.plugin.compose")
}

android {
    // Enable the Compose feature in the Android Gradle Plugin
    buildFeatures {
        compose = true
    }

    // Set the specific version of the Compose Compiler
    composeOptions {
        kotlinCompilerExtensionVersion = libs.versions.composeCompiler.get()
    }
}

// Configure Kotlin compiler for Compose experimental APIs
tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile>().configureEach {
    compilerOptions {
        // Opt-in to Compose experimental APIs
        freeCompilerArgs.addAll(
            listOf(
                "-opt-in=androidx.compose.material3.ExperimentalMaterial3Api",
                "-opt-in=androidx.compose.foundation.ExperimentalFoundationApi",
                "-opt-in=androidx.compose.animation.ExperimentalAnimationApi"
            )
        )
    }
}

dependencies {
    // Import the Compose Bill of Materials (BoM) to manage versions
    val composeBom = platform(libs.androidx.compose.bom)
    implementation(composeBom)
    androidTestImplementation(composeBom)

    // Add the essential Compose libraries
    implementation(libs.compose.ui)
    implementation(libs.compose.ui.graphics)
    implementation(libs.compose.ui.tooling.preview)
    implementation(libs.compose.material3)
    implementation(libs.compose.animation)

    // Compose integration with AndroidX
    implementation(libs.androidx.lifecycle.viewmodel.compose)
    implementation(libs.androidx.lifecycle.runtime.compose)
    implementation(libs.androidx.navigation.compose)

    // Debug-only tooling
    debugImplementation(libs.compose.ui.tooling)
}
