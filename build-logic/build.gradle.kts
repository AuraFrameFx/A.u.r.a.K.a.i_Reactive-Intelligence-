plugins {
    `kotlin-dsl`        // applies java-gradle-plugin
}

// Configure Kotlin compilation to match Java toolchain
tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinJvmCompile>().configureEach {
    jvmTargetValidationMode.set(org.jetbrains.kotlin.gradle.dsl.jvm.JvmTargetValidationMode.WARNING)
    // Kotlin 2.3.0-Beta2 supports up to JVM 24
    compilerOptions {
        jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_24)
    }
}

java {
    toolchain {
        // Java 24 - required by Firebase and other dependencies
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
    }
}

dependencies {
    // IMPORTANT: build-logic cannot use version catalog (libs.*) - builds BEFORE catalog available!
    // Use hardcoded versions matching settings.gradle.kts plugin declarations
    implementation("com.android.tools.build:gradle:9.0.0-alpha14")
    implementation("org.jetbrains.kotlin:kotlin-gradle-plugin:2.3.0-Beta2")
    implementation("com.google.dagger:hilt-android-gradle-plugin:2.57.2")
    implementation("com.google.devtools.ksp:symbol-processing-gradle-plugin:2.3.2")
    implementation("com.google.gms:google-services:4.4.4")
}




    // Kotlin Serialization plugin is bundled in kotlin-gradle-plugin, but we need to ensure it's accessible
    // The serialization plugin is applied via kotlin-gradle-plugin, no separate dependency needed

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
// For standard library module:
//   plugins {
//       id("genesis.android.library")  // All-in-one: Android, Hilt, Compose, KSP
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
