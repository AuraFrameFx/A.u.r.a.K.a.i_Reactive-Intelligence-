plugins {
    id("com.android.library") version "9.0.0-alpha13"
    id("com.google.dagger.hilt.android") version "2.57.2"
    id("com.google.devtools.ksp") version "2.3.0"
    id("org.jetbrains.kotlin.plugin.compose") version "2.2.21"
    id("org.jetbrains.kotlin.plugin.serialization") version "2.2.21"
}
android {
    namespace = "dev.aurakai.auraframefx.featuremodule"
    compileSdk = 36
}
dependencies {
    // Include local libs directory for compileOnly dependencies
    compileOnly(files("$projectDir/libs/api-82.jar"))
    ksp("com.github.LSPosed.YukiHookAPI:yuApiClient:1.3.1")


    // Libsu for root operations
    implementation(libs.libsu.core)
    implementation(libs.libsu.io)
    implementation(libs.libsu.service)
    api(libs.androidx.core.ktx) // if APIs leak types
    implementation(libs.androidx.appcompat)
    implementation(libs.timber)

    // If this library uses Compose UI:
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.compose.ui)
    implementation(libs.compose.material3)

    // Hilt in library
    implementation(libs.hilt.android)
    ksp(libs.hilt.compiler)

    // Xposed API and other compile-only dependencies
}
