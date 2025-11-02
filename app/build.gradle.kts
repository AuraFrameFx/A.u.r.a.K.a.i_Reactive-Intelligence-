plugins {
    id("com.android.application") version "9.0.0-alpha13" // This likely applies the base configurations already
    id("org.jetbrains.kotlin.plugin.compose") version "2.2.21"
    alias(libs.plugins.ksp)
    id("com.google.dagger.hilt.android") version "2.57.2"
    alias(libs.plugins.serialization)
    id("com.google.gms.google-services") version "4.4.4"
}


android {
    namespace = "dev.aurakai.auraframefx"
    compileSdk = 36
}

dependencies {
    implementation(libs.libsu.core)
    implementation(libs.libsu.io)
    implementation(libs.libsu.service)
    implementation("com.github.topjohnwu.libsu:service:6.0.0")
    // Xposed/YukiHook (use published coordinates; avoid local jars here)
    compileOnly(libs.xposed.api)
    compileOnly(libs.yukihookapi)
    val composeBom = platform("androidx.compose:compose-bom:2024.06.00") // Use a recent BOM version
    implementation(composeBom)
    androidTestImplementation(composeBom)

    // Add Compose material, UI, and tooling dependencies
    implementation(libs.androidx.ui)
    implementation(libs.material3)
    implementation(libs.androidx.activity.compose)
    // Project modules
    implementation(project(":core:data"))
    implementation(project(":core:ui"))
    implementation(project(":secure-comm"))
    implementation(project(":collab-canvas"))
    implementation(project(":extendsysa"))
    implementation(project(":extendsysb"))
    implementation(project(":extendsysc"))
    implementation(project(":extendsysd"))
    implementation(project(":extendsyse"))
    implementation(project(":extendsysf"))

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.material)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.lifecycle.viewmodel.ktx)
    coreLibraryDesugaring(libs.desugar.jdk.libs)

    // Compose
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.navigation.compose)
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.tooling.preview)
    debugImplementation(libs.androidx.compose.ui.tooling)
    debugImplementation(libs.androidx.compose.ui.test.manifest)
    implementation(libs.androidx.lifecycle.viewmodel.compose)

    // Room
    ksp(libs.androidx.room.compiler)

    // DataStore
    implementation(libs.androidx.datastore.preferences)
    implementation(libs.androidx.datastore.core)

    // WorkManager
    implementation(libs.androidx.work.runtime.ktx)

    // Firebase
    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.analytics.ktx)
    implementation(libs.firebase.crashlytics.ktx)
    implementation(libs.firebase.auth)
    implementation(libs.firebase.firestore.ktx)

    // Networking
    implementation(libs.retrofit)
    implementation(libs.okhttp)
    implementation(libs.retrofit.converter.moshi)
    implementation(libs.converter.kotlinx.serialization)
    implementation(libs.okhttp.logging.interceptor)

    // Kotlin
    implementation(libs.kotlinx.coroutines.android)
    implementation(libs.kotlinx.serialization.json)
    implementation(libs.kotlinx.datetime)

    // Dependency Injection
    implementation(libs.hilt.android)
    ksp(libs.hilt.compiler)
    implementation(libs.androidx.hilt.navigation)

    // Utilities
    implementation(libs.timber)
    implementation(libs.coil)
    implementation(libs.com.google.gms.google.services.gradle.plugin)
    implementation(libs.lottie.compose)
    implementation(libs.guava)
    implementation(libs.xz)

    // Xposed/Maven coordinates kept for compatibility
    compileOnly(libs.xposed.api)
    compileOnly(libs.yukihookapi)

    // Testing
    testImplementation(libs.junit)
    testImplementation(libs.mockk)
}
