// Root build.gradle.kts
// ═══════════════════════════════════════════════════════════════════════════
// A.u.r.a.K.a.I Reactive Intelligence - Root Build Configuration
// ═══════════════════════════════════════════════════════════════════════════

// Apply plugin version management to all projects
plugins {
    // Base plugins with versions
    id("org.jetbrains.kotlin.android") version "2.3.0-Beta2" apply false
    id("org.jetbrains.kotlin.plugin.compose") version "2.3.0-Beta2" apply false
    id("org.jetbrains.kotlin.plugin.serialization") version "2.3.0-Beta2" apply false
    id("org.jetbrains.kotlin.plugin.parcelize") version "2.3.0-Beta2" apply false

    // Android plugins
    id("com.android.application") version "9.0.0-alpha14" apply false
    id("com.android.library") version "9.0.0-alpha14" apply false

    // Other plugins
    id("com.google.dagger.hilt.android") version "2.57.2" apply false
    id("com.google.devtools.ksp") version "2.3.2" apply false
    id("com.google.gms.google-services") version "4.4.4" apply false
    id("com.google.firebase.crashlytics") version "3.0.6" apply false
}

// Clean task for the root project
tasks.register("clean", Delete::class) {
    delete(rootProject.layout.buildDirectory)
}

// Configure all projects
allprojects {
    // Common configurations can go here
    group = "dev.aurakai.auraframefx"
    version = "0.1.0"

    // Apply common repositories
}

