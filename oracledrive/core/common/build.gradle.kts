plugins {
    id("com.android.library") version "9.0.0-alpha13" // This likely applies the base configurations already
    id("org.jetbrains.kotlin.plugin.compose") version "2.2.21"
    id("com.google.devtools.ksp") version "2.3.0"
    id("com.google.dagger.hilt.android") version "2.57.2"
    alias(libs.plugins.serialization)
}
android {
    namespace = "dev.aurakai.auraframefx.core.common"
    compileSdk = 36


}

dependencies {
    // Utilities and extensions
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.security.crypto)

    // --- Jetpack Compose (versions are managed by the BOM from the convention plugin) ---
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.material3) // Standardize on Material 3 for Compose
    implementation(libs.androidx.navigation.compose)
    debugImplementation(libs.androidx.compose.ui.tooling)

    // --- Lifecycle ---
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.lifecycle.viewmodel.ktx)
    implementation(libs.androidx.lifecycle.viewmodel.compose)

    // --- Hilt (plugin applied by `genesis.android.library`) ---
    implementation(libs.hilt.android)
    ksp(libs.hilt.compiler)
    implementation(libs.androidx.hilt.navigation.compose) // Ensure this alias points to the compose version

    // --- Room (KSP is applied by convention plugin) ---
    ksp(libs.androidx.room.compiler)

    // --- DataStore ---
    implementation(libs.androidx.datastore.preferences)
    implementation(libs.androidx.datastore.core)

    // --- Coroutines & Serialization ---
    implementation(libs.kotlinx.coroutines.core)
    implementation(libs.kotlinx.serialization.json)
    implementation(libs.kotlinx.datetime)

    // --- Networking ---
    implementation(libs.retrofit) // Assuming you have clearer aliases
    implementation(libs.okhttp)
    implementation(libs.okhttp.logging.interceptor)
    // Standardize on one converter. kotlinx.serialization is the modern default.
    implementation(libs.retrofit.converter.kotlinx.serialization)

    // --- Firebase ---
    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.auth)

    // --- Background Work ---
    implementation(libs.androidx.work.runtime.ktx)

    // --- Utilities (Logging, Root) ---
    implementation(libs.timber)
    implementation(libs.libsu.core)
    implementation(libs.libsu.io)

    // --- Hooking Frameworks (Compile Only) ---
    compileOnly(libs.xposed.api)
    compileOnly(libs.yukihookapi)


    // --- Desugaring (Enabled by convention plugin) ---
    coreLibraryDesugaring(libs.desugar.jdk.libs)

    // --- Testing ---
    testImplementation(libs.junit.jupiter.api)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.hilt.android.testing)
    androidTestImplementation(libs.androidx.benchmark.junit4)
    androidTestImplementation(libs.androidx.test.uiautomator)
}
