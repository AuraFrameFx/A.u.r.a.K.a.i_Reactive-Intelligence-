// Precompiled script plugin for Hilt dependency injection
// Apply this to any library or app that needs Hilt

plugins {
    // Hilt requires the Google Dagger Hilt plugin
    alias(libs.plugins.hilt)
}

dependencies {
    // Add the core Hilt dependencies
    // KSP is assumed to be applied by the base library/application plugin
    implementation(libs.hilt.android)
    ksp(libs.hilt.compiler)

    // Hilt Navigation Compose (for ViewModel injection in Compose)
    implementation(libs.androidx.hilt.navigation.compose)

    // Hilt WorkManager support (for injecting dependencies into Workers)
    implementation(libs.androidx.hilt.work)
}
