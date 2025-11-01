plugins {
    `kotlin-dsl`
}

// The java block configures the toolchain and then CLOSES.
java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(24))
    }
} // <-- This closing brace is crucial.

// These blocks MUST be at the top level, AFTER the java block.
dependencies {
    implementation("com.android.tools.build:gradle:9.0.0-alpha13")
    implementation("com.google.dagger:hilt-android-gradle-plugin:2.57.2")
    implementation("org.jetbrains.kotlin:kotlin-gradle-plugin:2.3.0-Beta2")
    implementation("com.google.gms:google-services:4.4.4")

    testImplementation(gradleTestKit())
    testImplementation("org.junit.jupiter:junit-jupiter-api:6.0.0")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:6.0.0")
}

gradlePlugin {
    plugins {
        register("com.android.application") {
            id = "genesis.android.application"
            implementationClass = "plugins.GenesisApplicationPlugin"
        }
        register("com.android.library") {
            id = "genesis.android.library"
            implementationClass = "plugins.GenesisLibraryPlugin"
        }
    }
}
