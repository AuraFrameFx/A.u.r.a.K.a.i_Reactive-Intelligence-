plugins {
    id("com.android.library")
    id("com.google.dagger.hilt.android")
    id("org.jetbrains.kotlin.android")
    id("com.google.devtools.ksp")
    id("org.jetbrains.kotlin.plugin.compose") version "2.2.21"
    id("org.jetbrains.kotlin.plugin.serialization") version "2.2.21"
}


android {
    namespace = "dev.aurakai.auraframefx.datavein.oracle"
    compileSdk = libs.versions.compile.sdk.get().toInt()
    defaultConfig {
        minSdk = libs.versions.min.sdk.get().toInt()
    }

    dependencies {
        // Include local libs directory for compileOnly dependencies
        compileOnly(files("datavein-oracle-native/libs/api-82.jar"))
        compileOnly(files("datavein-oracle-native/libs/api-82-sources.jar"))
        
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
}
