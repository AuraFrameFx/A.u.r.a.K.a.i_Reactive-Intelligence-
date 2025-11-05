plugins {
    id("com.android.library") version "9.0.0-alpha13"
    id("com.google.dagger.hilt.android") version "2.57.2"
    id("com.google.devtools.ksp") version "2.3.0"
    id("org.jetbrains.kotlin.plugin.compose") version "2.2.21"
    id("org.jetbrains.kotlin.plugin.serialization") version "2.2.21"

}
android {
    namespace = "dev.aurakai.auraframefx.securecomm"
    compileSdk = 36

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_24
        targetCompatibility = JavaVersion.VERSION_24
    }
}
dependencies {
    compileOnly(files("$projectDir/libs/api-82.jar"))
    ksp("com.highcapable.yukihookapi:ksp-xposed:1.3.1")

    implementation(libs.libsu.core)
    implementation(libs.libsu.io)
    implementation(libs.libsu.service)

    // Core AndroidX dependencies
    api(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.timber)

    // BouncyCastle for cryptography
    implementation("org.bouncycastle:bcprov-jdk18on:1.78.1")

    // Compose UI
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.compose.ui)
    implementation(libs.compose.material3)
    implementation(libs.compose.ui.tooling.preview)
    debugImplementation(libs.compose.ui.tooling)

    // Hilt for dependency injection
    implementation(libs.hilt.android)
    ksp(libs.hilt.compiler)

    // Kotlin coroutines
    implementation(libs.kotlinx.coroutines.android)
    implementation(libs.kotlinx.coroutines.core)
}
