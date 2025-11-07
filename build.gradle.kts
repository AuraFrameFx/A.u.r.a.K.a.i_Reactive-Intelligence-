// Root build.gradle.kts
// ═══════════════════════════════════════════════════════════════════════════
// A.u.r.a.K.a.I Reactive Intelligence - Root Build Configuration
// ═══════════════════════════════════════════════════════════════════════════
//
// This is the root build file for the entire multi-module project.
// Its ONLY purpose is to define the root-level clean task.
//
// ALL configuration logic has been moved to convention plugins in build-logic/
// This follows modern Gradle best practices for large multi-module projects.
//
// Genesis Convention Plugins Available:
//   • genesis.android.application - For the :app module
//   • genesis.android.library     - For Android library modules
//   • genesis.android.base        - Foundational Android configuration (auto-applied)
//
// ═══════════════════════════════════════════════════════════════════════════

tasks.register("clean", Delete::class) {
    delete(rootProject.layout.buildDirectory)
}
