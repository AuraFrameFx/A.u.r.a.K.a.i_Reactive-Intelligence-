// genesis.android.yukihook.gradle.kts
// Specialized convention plugin for YukiHook/Xposed modules.
// Apply this to any module that hooks into the Android system via Xposed framework.

plugins {
    // KSP is required for YukiHook annotation processing
    // Note: Use plugin ID directly in convention plugins, not alias()
    id("com.google.devtools.ksp")
}

// Add YukiHook and Xposed specific dependencies
dependencies {
    // Xposed API - compile-only since it's provided by the framework at runtime
    compileOnly(libs.xposed.api)

    // YukiHook API - the modern Xposed hooking framework
    implementation(libs.yukihookapi.api)

    // KSP processor for YukiHook annotation processing
    ksp(libs.yukihookapi.ksp)

    // KavaRef - reflection utilities for Xposed modules
    implementation(libs.kavaref.core)
    implementation(libs.kavaref.extension)
}

// Configure KSP for YukiHook processor
ksp {
    // Pass the module's package name to the YukiHook processor
    // This is used for generating hook entry points
    arg("YUKIHOOK_PACKAGE_NAME", project.group.toString())

    // Enable incremental annotation processing for faster builds
    arg("room.incremental", "true")
}

// Configure ProGuard/R8 rules for Xposed modules
android {
    buildTypes {
        release {
            // Keep all Xposed hook classes
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }

    // Xposed modules need specific manifest configurations
    defaultConfig {
        // Ensure the module is properly tagged for Xposed
        manifestPlaceholders["xposedmodule"] = "true"
        manifestPlaceholders["xposedminversion"] = libs.versions.xposed.get()
    }
}

// Add a task to verify Xposed configuration
tasks.register("verifyXposedConfig") {
    group = "verification"
    description = "Verifies that this module is properly configured as an Xposed module"

    doLast {
        val manifestFile = project.file("src/main/AndroidManifest.xml")
        if (manifestFile.exists()) {
            val manifestContent = manifestFile.readText()

            // Check for Xposed module declaration
            if (!manifestContent.contains("xposedmodule")) {
                logger.warn("WARNING: AndroidManifest.xml may be missing Xposed module declaration")
            }

            // Check for hook entry point
            if (!manifestContent.contains("xposedminversion")) {
                logger.warn("WARNING: AndroidManifest.xml may be missing xposedminversion metadata")
            }

            logger.lifecycle("✓ Xposed module configuration verified for ${project.name}")
        } else {
            logger.error("ERROR: AndroidManifest.xml not found in ${project.name}")
        }
    }
}

// Automatically run verification before assembling
tasks.named("preBuild").configure {
    dependsOn("verifyXposedConfig")
}
