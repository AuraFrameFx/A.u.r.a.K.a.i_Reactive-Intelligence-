plugins {
    id("genesis.android.library.hilt")
}

android {
    namespace = "dev.aurakai.auraframefx.aura.reactivedesign.auraslab"
}

dependencies {
    // ═══════════════════════════════════════════════════════════════════════════
    // NOTE: The following are AUTOMATICALLY provided by genesis.android.library:
    // - Hilt Android + Compiler (DI)
    // - Core KTX, AppCompat
    // - Kotlin Coroutines (Core + Android)
    // - Kotlinx Serialization JSON
    // - Timber (logging)
    // - Desugar JDK Libs (Java 24 support)
    // - Xposed API, LibXposed, EzXHelper
    //
    // You only need to declare module-specific dependencies below!
    // ═══════════════════════════════════════════════════════════════════════════


    // Compose UI (use BOM for version management)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.compose.ui)
    implementation(libs.compose.ui.graphics)
    implementation(libs.compose.ui.tooling.preview)
    implementation(libs.compose.material3)
    implementation(libs.compose.animation)
    implementation(libs.compose.material.icons.extended)
    debugImplementation(libs.compose.ui.tooling)

    // Lifecycle & ViewModel
    implementation(libs.androidx.lifecycle.runtime.compose)
    implementation(libs.androidx.lifecycle.viewmodel.compose)

    // Navigation
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.navigation.compose)
    implementation(libs.androidx.hilt.navigation.compose)

    // Material Design
    implementation(libs.androidx.material)

    // Room Database
    implementation(libs.androidx.room.runtime)
    implementation(libs.androidx.room.ktx)
    ksp(libs.androidx.room.compiler)

    // DataStore
    implementation(libs.androidx.datastore.preferences)
    implementation(libs.androidx.datastore.core)

    // Kotlinx DateTime
    implementation(libs.kotlinx.datetime)

    // Networking (Retrofit + OkHttp)
    implementation(libs.retrofit)
    implementation(libs.retrofit.converter.moshi)
    implementation(libs.retrofit.converter.kotlinx.serialization)
    implementation(libs.okhttp)
    implementation(libs.okhttp.logging.interceptor)

    // WorkManager
    implementation(libs.androidx.work.runtime.ktx)

    // Security
    implementation(libs.androidx.security.crypto)

    // Firebase (via BOM)
    implementation(platform(libs.firebase.bom))

    // Root Access (LibSu)
    implementation(libs.libsu.core)
    implementation(libs.libsu.io)

    // YukiHookAPI (advanced hooking)
    implementation(libs.yukihookapi.api)
    ksp(libs.yukihookapi.ksp)

    // Testing
    testImplementation(libs.androidx.junit)
    androidTestImplementation(platform(libs.androidx.compose.bom))
}
