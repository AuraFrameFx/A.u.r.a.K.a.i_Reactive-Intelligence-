// Precompiled script plugin for Room Database
// Apply this to any module that uses Room for local persistence

plugins {
    // Room requires KSP for code generation
    // Note: Use plugin ID directly in convention plugins, not alias()
    id("com.google.devtools.ksp")
}

// Configure Room schema directory for database migrations
android {
    defaultConfig {
        javaCompileOptions {
            annotationProcessorOptions {
                arguments += mapOf(
                    "room.schemaLocation" to "$projectDir/schemas",
                    "room.incremental" to "true"
                )
            }
        }
    }
}

dependencies {
    // Room runtime
    implementation(libs.androidx.room.runtime)
    implementation(libs.androidx.room.ktx)

    // Room compiler (KSP)
    ksp(libs.androidx.room.compiler)

    // Testing
    testImplementation(libs.androidx.room.testing)
}
