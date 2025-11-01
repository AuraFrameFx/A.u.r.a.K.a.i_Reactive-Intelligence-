plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.ksp)
    alias(libs.plugins.hilt)

}

android {
    namespace = "dev.aurakai.auraframefx.collab_canvas"
    compileSdk = 36
    defaultConfig {
        minSdk = 34

}
dependencies {
    // Module dependencies - depend on core modules only
    implementation(project(":core:data"))
    implementation(project(":core:ui"))
    implementation(libs.libsu.core)
    implementation(libs.libsu.io)
    implementation(libs.libsu.core)

    // Hooking/runtime-only compile-time APIs for modules that interact with Xposed/YukiHook
    compileOnly(libs.yukihookapi)
    compileOnly(libs.xposed.api)

    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.material)
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.lifecycle.viewmodel.ktx)
    implementation(libs.androidx.lifecycle.viewmodel.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.material)
    implementation(libs.material3)
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.navigation.compose)
    debugImplementation(libs.androidx.compose.ui.tooling)

    ksp(libs.androidx.room.compiler)

    implementation(libs.androidx.datastore.preferences)
    implementation(libs.androidx.datastore.core)

    implementation(libs.hilt.android)
    ksp(libs.hilt.compiler)

    implementation(libs.kotlinx.serialization.json)
    implementation(libs.kotlinx.datetime)
    implementation(libs.coroutines)
    implementation(libs.retrofit)
    implementation(libs.okhttp)
    implementation(libs.retrofit.converter.moshi)
    implementation(libs.converter.kotlinx.serialization)
    implementation(libs.okhttp.logging.interceptor)
    implementation(libs.androidx.work.runtime.ktx)
    implementation(libs.androidx.security.crypto)
    implementation(libs.androidx.hilt.navigation)

    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.auth)

    implementation(libs.timber)

    coreLibraryDesugaring(libs.desugar.jdk.libs)

    testImplementation(libs.junit.jupiter.api)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.hilt.android.testing)
    androidTestImplementation(libs.androidx.benchmark.junit4)
    androidTestImplementation(libs.androidx.test.uiautomator)
}

tasks.register("collabStatus") {
    group = "aegenesis"
    doLast {
        println("COLLAB CANVAS - Ready (Java 24 toolchain, unified).")
    }
}
}
