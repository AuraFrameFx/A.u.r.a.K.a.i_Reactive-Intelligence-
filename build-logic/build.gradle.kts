plugins {
    `kotlin-dsl`
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(21))
    }
}

dependencies {
    // Plugin dependencies for convention plugins
    // These allow the convention plugins to apply Android, Kotlin, Hilt, and KSP plugins
    implementation(libs.gradle.plugin)
    implementation(libs.kotlin.gradle.plugin)
    implementation(libs.hilt.gradle.plugin)
    implementation(libs.ksp.gradle.plugin)
}

// Precompiled script plugins are automatically registered!
// The following plugins are available:
//   - genesis.android.application (from genesis.android.application.gradle.kts)
//   - genesis.android.library (from genesis.android.library.gradle.kts)
//   - genesis.android.hilt (from genesis.android.hilt.gradle.kts)
//   - genesis.android.compose (from genesis.android.compose.gradle.kts)
//   - genesis.android.room (from genesis.android.room.gradle.kts)
//   - genesis.kotlin.jvm (from genesis.kotlin.jvm.gradle.kts)
//
// No manual registration needed - the file name becomes the plugin ID!
