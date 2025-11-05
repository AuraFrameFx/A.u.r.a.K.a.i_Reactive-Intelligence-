plugins {
    id("com.android.library") version "9.0.0-alpha13"
    id("com.google.dagger.hilt.android") version "2.57.2"
    id("com.google.devtools.ksp") version "2.3.0"
    id("org.jetbrains.kotlin.plugin.compose") version "2.2.21"
    id("org.jetbrains.kotlin.plugin.serialization") version "2.2.21"

}
android {
    namespace = "dev.aurakai.auraframefx.romtools"
    compileSdk = 36

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_24
        targetCompatibility = JavaVersion.VERSION_24
    }
}
dependencies {
    compileOnly(files("$projectDir/libs/api-82.jar"))
    ksp("com.highcapable.yukihookapi:ksp-xposed:1.3.1")


    // Libsu for root operations
    implementation(libs.libsu.core)
    implementation(libs.libsu.io)
    implementation(libs.libsu.service)

    // Core AndroidX dependencies
    api(libs.androidx.core.ktx) // if APIs leak types
    implementation(libs.androidx.appcompat)
    implementation(libs.timber)

    // Compose / Lifecycle / Navigation / Hilt integrations (single declarations)
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.navigation.compose)
    implementation(libs.androidx.hilt.navigation.compose)
    implementation(libs.androidx.lifecycle.viewmodel.compose)
    implementation(libs.androidx.lifecycle.runtime.compose)

    // Work
    implementation(libs.androidx.work.runtime.ktx)

    // Kotlin Serialization
    implementation(libs.kotlinx.serialization.json)

    // Compose UI
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.compose.ui)
    implementation(libs.compose.material3)
    implementation(libs.compose.material.icons.extended)  // For extended Material Icons
    implementation(libs.compose.ui.tooling.preview)
    debugImplementation(libs.compose.ui.tooling)

    // Hilt for dependency injection
    implementation(libs.hilt.android)
    implementation(libs.androidx.hilt.navigation.compose)  // For hiltViewModel()
    ksp(libs.hilt.compiler)

    // Kotlin coroutines
    implementation(libs.kotlinx.coroutines.android)
    implementation(libs.kotlinx.coroutines.core)
}

// Force a single annotations artifact to avoid duplicate-class errors seen in the build logs.
configurations.all {
    resolutionStrategy {
        force("org.jetbrains:annotations:23.0.0")
    }
}
