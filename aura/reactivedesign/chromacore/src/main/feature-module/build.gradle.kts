plugins {
    id("com.android.library") version "9.0.0-alpha10"
    id("com.google.devtools.ksp") version "2.3.0"
}


android {
    namespace = "dev.aurakai.auraframefx.java"
    compileSdk = 36

}


dependencies {
    implementation("com.github.topjohnwu.libsu:core:5.0.4")
    implementation("com.github.topjohnwu.libsu:io:5.0.4")

    implementation("com.jakewharton.timber:timber:5.0.1")
    implementation("com.google.dagger:hilt-android:2.57.2")
    implementation("com.google.dagger:hilt-android-compiler:2.57.2")
    implementation("com.google.android.material:material:1.13.0")
    implementation("androidx.compose.ui:ui-tooling-preview:1.9.4")
    implementation("androidx.compose.ui:ui-tooling:1.9.4")
    implementation("androidx.compose.ui:ui-test-junit4:1.9.4")
    implementation("androidx.compose.ui:ui-test-manifest:1.9.4")
    implementation("androidx.compose.ui:ui-test:1.9.4")
    implementation("androidx.compose.ui:ui-test-junit4:1.9.4")
    implementation("androidx.compose.ui:ui-test-manifest:1.9.4")
    implementation(project(":build-logic"))
    implementation(libs.hilt.android)
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.lifecycle.viewmodel.ktx)
    coreLibraryDesugaring(libs.desugar.jdk.libs)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.navigation.compose)
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.compose.material3)
    implementation(libs.bundles.lifecycle)
    implementation(libs.bundles.room)
    implementation(libs.androidx.datastore.preferences)
    implementation(libs.androidx.datastore.core)
    implementation(libs.kotlinx.serialization.json)
    implementation(libs.kotlinx.datetime)
    implementation(libs.bundles.coroutines)
    implementation(libs.bundles.network)
    implementation(platform(libs.firebase.bom))
    implementation(libs.bundles.firebase)
    implementation(libs.hilt.compiler)
    implementation(libs.androidx.room.compiler)
    implementation("com.google.android.material:compose-theme-adapter-3:1.1.1")
    implementation("com.google.firebase:firebase-auth-ktx:23.2.1")
    // Guard local Xposed API JARs — check module libs/ first then ../Libs/ fallback
    val candidateLibDirs = listOf(projectDir.resolve("libs"), projectDir.parentFile.resolve("Libs"))
    val api82File = candidateLibDirs.map { java.io.File(it, "api-82.jar") }.firstOrNull { it.exists() }
    val api82SourcesFile = candidateLibDirs.map { java.io.File(it, "api-82-sources.jar") }.firstOrNull { it.exists() }

    if (api82File != null) {
        compileOnly(files(api82File))
    } else {
        logger.warn("Missing local Xposed JAR (api-82.jar). Checked: ${candidateLibDirs.joinToString()}")
    }
    if (api82SourcesFile != null) {
        compileOnly(files(api82SourcesFile))
    } else {
        logger.warn("Missing local Xposed sources JAR (api-82-sources.jar). Checked: ${candidateLibDirs.joinToString()}")
    }

    implementation(libs.androidx.material)
    testImplementation(libs.bundles.testing.unit)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.hilt.android.testing)
    debugImplementation(libs.leakcanary.android)
    implementation(libs.androidx.hilt.navigation.compose)

    // Explicit androidx versions requested by the user (added alongside existing libs entries)
    implementation("androidx.core:core-ktx:1.17.0")
    implementation("androidx.appcompat:appcompat:1.7.1")
    implementation(platform("androidx.compose:compose-bom:2025.10.00"))
    implementation("androidx.activity:activity-compose:1.11.0")
    implementation("androidx.navigation:navigation-compose:2.9.5")
    implementation("androidx.compose.ui:ui:1.9.4")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.9.4")
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.9.4")
    implementation("androidx.room:room-runtime:2.8.2")
    implementation("androidx.room:room-ktx:2.8.2")
    implementation("androidx.work:work-runtime-ktx:2.10.5")
    implementation("androidx.hilt:hilt-work:1.3.0")
    implementation("androidx.datastore:datastore-preferences:1.1.7")
    implementation("androidx.datastore:datastore-core:1.1.7")
    implementation("androidx.security:security-crypto:1.1.0")
    androidTestImplementation("androidx.benchmark:benchmark-junit4:1.4.1")
    androidTestImplementation("androidx.test.uiautomator:uiautomator:2.3.0")
}
