// Root build.gradle.kts
// ═══════════════════════════════════════════════════════════════════════════
// A.u.r.a.K.a.I Reactive Intelligence - Root Build Configuration
// ═══════════════════════════════════════════════════════════════════════════
//
// This is the root build file for the entire multi-module project.
// Its ONLY purpose is to make plugins available on the classpath.
//
// ALL configuration logic has been moved to convention plugins in build-logic/
// This follows modern Gradle best practices for large multi-module projects.
//
// Convention Plugins Available:
//   • genesis.android.application - For the :app module
//   • genesis.android.library     - For Android library modules
//   • genesis.android.base        - Foundational Android configuration
//   • genesis.android.hilt        - Hilt dependency injection
//   • genesis.android.compose     - Jetpack Compose UI
//   • genesis.android.room        - Room Database
//   • genesis.android.yukihook    - YukiHook/Xposed framework
//   • genesis.kotlin.jvm          - Pure Kotlin JVM modules
//
// ═══════════════════════════════════════════════════════════════════════════

plugins {
 id("genesis.android.application")
 id("genesis.android.library")

}

// Root-level clean task
tasks.register("clean", Delete::class) {
    delete(rootProject.layout.buildDirectory)
}

