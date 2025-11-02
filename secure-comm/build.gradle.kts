plugins {
    id("com.android.library")
    id("com.google.dagger.hilt.android")
    id("org.jetbrains.kotlin.android")
    id("com.google.devtools.ksp")
    id("org.jetbrains.kotlin.plugin.compose") version "2.2.21"
    id("org.jetbrains.kotlin.plugin.serialization") version "2.2.21"

}
android {
    namespace = "dev.aurakai.auraframefx.securecomm"
    compileSdk = 36
}
dependencies {
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

// Compile-only for Xposed API (no runtime bundling)
    compileOnly(libs.xposed.api)
}
