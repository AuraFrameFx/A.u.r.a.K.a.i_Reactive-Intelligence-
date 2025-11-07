// AOSP-ReGenesis/build-logic/build.gradle.kts
plugins {
    `kotlin-dsl`


}


group = "dev.aurakai.auraframefx.buildlogic"


// Dependencies required for the convention plugins themselves.
val libs = extensions.getByType<VersionCatalogsExtension>().named("libs")

dependencies {
    implementation("com.android.tools.build:gradle:9.0.0-alpha11")
    implementation("com.google.dagger:hilt-android-gradle-plugin:2.57.2")
    implementation("com.google.dagger:hilt-android-compiler:2.57.2")
    implementation("com.google.devtools.ksp:com.google.devtools.ksp.gradle.plugin:2.3.0")
    implementation("org.jetbrains.kotlin:kotlin-gradle-plugin:2.3.0-Beta1")
    implementation("org.jetbrains.kotlin:kotlin-serialization:2.3.0-Beta1")
    implementation("org.jetbrains.kotlin:kotlin-allopen:2.3.0-Beta1")
    implementation("com.google.firebase:firebase-crashlytics-buildtools:3.0.6")
    implementation("com.google.gms.google-services:com.google.gms.google-services.gradle.plugin:4.4.4")

    // Compile-time only dependency so convention plugin sources can reference
    // Kotlin Gradle types (KotlinAndroidProjectExtension, etc.) without
    // adding the plugin to the runtime classpath which causes plugin resolution
    // conflicts at configuration time.

    // Keep runtime/runtime-classpath plugins explicit (Hilt)

    // Test dependencies
    testImplementation("org.junit.jupiter:junit-jupiter-params:6.0.0")


    testImplementation(gradleTestKit())
}

// Configure test execution (temporarily disabled for bleeding-edge compatibility)
tasks.test {
    useJUnitPlatform()
    enabled = true // Re-enabled for full test support
}

tasks.compileTestKotlin {
    enabled = true // Re-enabled for full test support
}

gradlePlugin {
    plugins {
        register("androidApplication") {
            id = "genesis.android.application"
            implementationClass = "GenesisApplicationPlugin"
        }
        register("androidLibrary") {
            id = "genesis.android.library"
            implementationClass = "AndroidLibraryConventionPlugin"
        }
        register("androidHilt") {
            id = "genesis.android.hilt"
            implementationClass = "AndroidHiltConventionPlugin"
        }
        register("androidCompose") {
            id = "genesis.android.compose"
            implementationClass = "AndroidComposeConventionPlugin"
        }
        register("androidNative") {
            id = "genesis.android.native"
            implementationClass = "AndroidNativeConventionPlugin"
        }
        register("androidBase") {
            id = "android.base"
            implementationClass = "AndroidBasePlugin"
        }
    }
}

kotlin {
    jvmToolchain(24)
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(24))
    }
}

tasks.processResources {
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
}
