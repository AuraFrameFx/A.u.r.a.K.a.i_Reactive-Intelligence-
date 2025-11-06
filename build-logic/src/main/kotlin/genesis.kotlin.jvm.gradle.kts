// Precompiled script plugin for pure Kotlin JVM modules
// Use this for domain/data layers with no Android framework dependencies

plugins {
    // Note: Use kotlin() DSL or plugin ID directly in convention plugins, not alias()
    kotlin("jvm")
}

// No 'android' block needed!

java {
    sourceCompatibility = JavaVersion.VERSION_21
    targetCompatibility = JavaVersion.VERSION_21
}

// Configure Kotlin compiler options
tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinJvmCompile>().configureEach {
    compilerOptions {
        jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_21)

        // Enable progressive mode
        progressiveMode.set(true)

        // Opt-in to experimental APIs
        freeCompilerArgs.addAll(
            listOf(
                "-opt-in=kotlin.RequiresOptIn",
                "-opt-in=kotlinx.coroutines.ExperimentalCoroutinesApi"
            )
        )
    }
}

dependencies {
    // Essential Kotlin libraries for pure JVM modules
    implementation(libs.kotlinx.coroutines.core)

    // Testing
    testImplementation(libs.junit)
    testImplementation(libs.mockk)
}
