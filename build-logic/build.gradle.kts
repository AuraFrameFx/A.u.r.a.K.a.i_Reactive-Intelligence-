plugins {
    `kotlin-dsl`        // applies java-gradle-plugin
}

repositories {
    google()
    mavenCentral()
}

dependencies {
    // IMPORTANT: build-logic cannot use version catalog (libs.*) - builds BEFORE catalog available!
    // Use hardcoded versions matching settings.gradle.kts plugin declarations
    implementation("com.android.tools.build:gradle:9.0.0-alpha13")
    implementation("org.jetbrains.kotlin:kotlin-gradle-plugin:2.3.0-Beta2")
    implementation("com.google.dagger:hilt-android-gradle-plugin:2.57.2")
    implementation("com.google.devtools.ksp:symbol-processing-gradle-plugin:2.3.1")
    implementation("com.google.gms:google-services:4.4.4")
}

// ═══════════════════════════════════════════════════════════════════════════
// Genesis Convention Plugins Registration
// ═══════════════════════════════════════════════════════════════════════════
gradlePlugin {
    plugins {
        register("genesis.android.base") {
            id = "genesis.android.base"
            implementationClass = "plugins.GenesisBasePlugin"
        }
        register("genesis.android.library") {
            id = "genesis.android.library"
            implementationClass = "plugins.GenesisLibraryPlugin"
        }
        register("genesis.android.application") {
            id = "genesis.android.application"
            implementationClass = "plugins.GenesisApplicationPlugin"
        }
    }
}
