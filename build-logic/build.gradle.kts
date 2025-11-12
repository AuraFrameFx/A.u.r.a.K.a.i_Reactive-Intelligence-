plugins {
    `kotlin-dsl`        // applies java-gradle-plugin
}

// Configure Kotlin compilation to match Java toolchain
// MUST match the target used in GenesisApplicationPlugin and GenesisLibraryHiltPlugin (JVM 24)
tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinJvmCompile>().configureEach {
    compilerOptions {
        jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_24)
    }
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(24))
    }
}

gradlePlugin {
    plugins {
        register("genesisApplication") {
            id = "genesis.android.application"
            implementationClass = "GenesisApplicationPlugin"
        }
        register("genesisLibrary") {
            id = "genesis.android.library"
            implementationClass = "GenesisLibraryPlugin"
        }
        register("genesisLibraryHilt") {
            id = "genesis.android.library.hilt"
            implementationClass = "GenesisLibraryHiltPlugin"
        }
    }
}

dependencies {
    // IMPORTANT: build-logic cannot use version catalog (libs.*) - builds BEFORE catalog available!
    // Use hardcoded versions matching settings.gradle.kts plugin declarations
    implementation("com.android.tools.build:gradle:9.0.0-alpha14")
    implementation("org.jetbrains.kotlin:kotlin-gradle-plugin:2.3.0-Beta2")

    implementation("org.jetbrains.kotlin:compose-compiler-gradle-plugin:2.3.0-Beta2")
    implementation("org.jetbrains.kotlin:kotlin-serialization:2.3.0-Beta2")

    // Hilt Gradle Plugin - exclude ALL Android runtime (AAR) dependencies
    // build-logic is JVM-only and cannot consume Android AAR files
    // Must exclude all AndroidX transitive dependencies pulled by hilt-android-gradle-plugin
    implementation("com.google.dagger:hilt-android-gradle-plugin:2.57.2") {
        exclude(group = "com.google.dagger", module = "hilt-android")
        exclude(group = "androidx.activity")
        exclude(group = "androidx.fragment")
        exclude(group = "androidx.lifecycle")
        exclude(group = "androidx.savedstate")
        exclude(group = "androidx.annotation")
    }

    implementation("com.google.devtools.ksp:symbol-processing-gradle-plugin:2.3.2")
    implementation("com.google.gms:google-services:4.4.4")
}

// After applying plugins
dependencies {
    add("implementation", "com.google.dagger:hilt-android:2.57.2")
    add("implementation", "com.google.dagger:hilt-android-compiler:2.57.2")
    add("implementation", "org.jetbrains.kotlin:kotlin-gradle-plugin:2.3.0-Beta2")
}
// ═══════════════════════════════════════════════════════════════════════════
// Genesis Convention Plugins Registration
// ═══════════════════════════════════════════════════════════════════════════
//
// These are the PRIMARY convention plugins that modules should use.
// They are Kotlin class plugins (not precompiled scripts) for maximum control
// over plugin application order.
//


// ═══════════════════════════════════════════════════════════════════════════
// CORRECT USAGE EXAMPLES
// ═══════════════════════════════════════════════════════════════════════════
//
// For :app module:
//   plugins {
//       id("genesis.android.application")  // All-in-one: Android, Hilt, KSP, Compose, Serialization, Firebase
//   }
//
// For standard library module WITHOUT Hilt:
//   plugins {
//       id("genesis.android.library")  // Base library: Android, Compose, Serialization (NO Hilt)
//   }
//
// For library module WITH Hilt:
//   plugins {
//       id("genesis.android.library.hilt")  // Library with Hilt DI + KSP
//   }
//
// For YukiHook/Xposed module:
//   plugins {
//       id("genesis.android.library")   // Base library with Hilt, Compose, KSP
//       id("genesis.android.yukihook")  // Add YukiHook/Xposed support
//   }
//
// For Room database module:
//   plugins {
//       id("genesis.android.library")  // Base library
//       id("genesis.android.room")     // Add Room Database
//   }
//
// ═══════════════════════════════════════════════════════════════════════════
