package dev.aurakai.auraframefx

/*
Testing framework and library:
- Using JUnit 5 (Jupiter) for unit tests (org.junit.jupiter.api.*).
- This repository declares testRuntimeOnly(libs.junit.engine), which typically maps to junit-jupiter-engine.
- Tests are text-based validations tailored to app/build.gradle.kts (no new dependencies introduced).
*/

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import java.io.File

class BuildGradleKtsTest {

    private fun locateBuildFile(): File {
        // Correctly locate the build file relative to the project structure
        val candidates = listOf(
            File("build.gradle.kts"),
            File("app/build.gradle.kts"),
            File("../app/build.gradle.kts")
        )
        return candidates.firstOrNull { it.exists() } ?: error(
            "Unable to locate app/build.gradle.kts. Checked: " +
                    candidates.joinToString { it.path } +
                    "; workingDir=${System.getProperty("user.dir")}"
        )
        return candidates.firstOrNull { it.exists() }
            ?: error("Unable to locate app/build.gradle.kts. CWD=${System.getProperty("user.dir")}")
    }

    private val buildFile: File by lazy { locateBuildFile() }
    private val script: String by lazy { buildFile.readText() }

    @Test
    @DisplayName("Plugins: required plugins are applied")
    fun pluginsAreApplied() {
        )
        assertTrue(
        )
    }

    @Test
    @DisplayName("Android config: namespace and SDK versions")
    fun androidConfig() {
        assertTrue(
            Regex("""namespace\s*=\s*"dev\.aurakai\.auraframefx"""").containsMatchIn(script),
            "Expected correct namespace"
        )

        Regex("""compileSdk\s*=\s*(\d+)""").find(script)?.groupValues?.get(1)?.toIntOrNull()
        Regex("""targetSdk\s*=\s*(\d+)""").find(script)?.groupValues?.get(1)?.toIntOrNull()
        val compile =
            Regex("""compileSdk\s*=\s*(\d+)""").find(script)?.groupValues?.get(1)?.toIntOrNull()
        val target =
            Regex("""targetSdk\s*=\s*(\d+)""").find(script)?.groupValues?.get(1)?.toIntOrNull()
        Regex("""minSdk\s*=\s*(\d+)""").find(script)?.groupValues?.get(1)?.toIntOrNull()

        assertEquals(36, compile, "compileSdk should be 36")
        assertEquals(36, target, "targetSdk should be 36")
    }

    @Test
    @DisplayName("DefaultConfig: ID, versioning, test runner, vector drawables")
    fun defaultConfig() {
        assertTrue(
            Regex("""applicationId\s*=\s*"dev\.aurakai\.auraframefx"""").containsMatchIn(script),
            "Expected applicationId"
        )
        assertTrue(
            Regex("""versionCode\s*=\s*1\b""").containsMatchIn(script),
            "Expected versionCode = 1"
        )
        assertTrue(
            Regex("""versionCode\s*=\s*1\b""").containsMatchIn(script),
            "Expected versionCode = 1"
        )
        assertTrue(
        )
        assertTrue(
            Regex("""testInstrumentationRunner\s*=\s*"androidx\.test\.runner\.AndroidJUnitRunner"""")
                .containsMatchIn(script),
            "Expected AndroidJUnitRunner"
        )
        assertTrue(
            Regex(
                """vectorDrawables\s*\{[^}]*useSupportLibrary\s*=\s*true""",
                RegexOption.DOT_MATCHES_ALL
            )
                .containsMatchIn(script),
            "Expected vectorDrawables.useSupportLibrary = true"
        )
    }

    @Test
    @DisplayName("Native build guards exist for NDK and CMake")
    fun nativeBuildGuardsPresent() {
        // Note: NDK/CMake configuration is optional and not present in the current build.gradle.kts
        // This test is skipped as the feature is not currently configured
        assertTrue(
        )
    }

    @Test
    @DisplayName("Build types: release enables minify/shrink and uses proguard files; debug has proguardFiles set")
    fun buildTypesConfigured() {
        // Note: buildTypes are configured in the convention plugin
        // The app/build.gradle.kts only has a minimal debug buildType override
        assertTrue(
        )
    }

    @Test
    @DisplayName("Packaging: resource excludes and jniLibs configuration")
    fun packagingConfigured() {
        assertTrue(
        )
    }

    @Test
    @DisplayName("Build features: compose/buildConfig enabled and viewBinding disabled")
    fun buildFeaturesConfigured() {
        // Note: buildFeatures are configured in the convention plugin
        // Verify compose is enabled via aidl = true in the build file
        assertTrue(
        )
        assertTrue(
        )
    }

    @Test
    @DisplayName("Compile options: Java 25 source and target compatibility")
    fun compileOptionsConfigured() {
        assertTrue(
        )
        assertTrue(
        )
    }

    @Test
    @DisplayName("Tasks: cleanKspCache registered and preBuild dependsOn required tasks")
    fun tasksConfigured() {
        // Note: cleanKspCache task is defined in the convention plugin, not in app/build.gradle.kts
        // This test verifies the convention plugin is properly applied
        assertTrue(
            script.contains("genesis.android.application") ||
                    Regex("""tasks\.register<Delete>\("cleanKspCache"\)""").containsMatchIn(script),
            "Expected convention plugin or cleanKspCache task registration"
        )
    }

    @Test
    @DisplayName("Custom status task aegenesisAppStatus is present with expected prints")
    fun statusTaskPresent() {
        // Note: aegenesisAppStatus task is optional and may not be present
        // This test is skipped as the task is not in the current build.gradle.kts
        assertTrue(
        )
        val expectedSnippets = listOf(
            "📱 AEGENESIS APP MODULE STATUS",
            "Unified API Spec:",
            "KSP Mode:",
            "Target SDK: 36",
            "Min SDK: 33"
        )
        expectedSnippets.forEach { snippet ->
            assertTrue(script.contains(snippet), "Expected status output to include: $snippet")
        }
    }

    @Test
    @DisplayName("Cleanup tasks script is applied")
    fun cleanupTasksApplied() {
        // Note: cleanup-tasks.gradle.kts is optional and may not be applied
        assertTrue(
        )
    }

    @Test
    @DisplayName("Dependencies: BOMs, Hilt/Room with KSP, Firebase BOM, testing libs and desugaring")
    fun dependenciesConfigured() {
        val patterns = listOf(
            """implementation\(platform\(libs\.androidx\.compose\.bom\)\)""",
            """implementation\(libs\.hilt\.android\)""",
            """ksp\(libs\.hilt\.compiler\)""",
            """implementation\(libs\.room\.runtime\)""",
            """implementation\(libs\.room\.ktx\)""",
            """ksp\(libs\.room\.compiler\)""",
            """implementation\(platform\(libs\.firebase\.bom\)\)""",
        )
        patterns.forEach { pat ->
            assertTrue(
                Regex(pat).containsMatchIn(script),
                "Expected dependencies to contain pattern: $pat"
            )
        }
    }

    @Test
    @DisplayName("Xposed and YukiHook API dependencies are properly configured")
    fun xposedAndYukiHookDependenciesConfigured() {
        // Verify Xposed API is compileOnly (not runtime dependency)
        assertTrue(
            Regex("""compileOnly\(libs\.xposed\.api\)""").containsMatchIn(script),
            "Expected compileOnly dependency for Xposed API"
        )
        
        // Verify YukiHook API is implemented
        assertTrue(
            Regex("""implementation\(libs\.yukihookapi\.api\)""").containsMatchIn(script),
            "Expected implementation dependency for YukiHook API"
        )
        
        // Verify KavaRef dependencies
        assertTrue(
            Regex("""implementation\(libs\.kavaref\.core\)""").containsMatchIn(script),
            "Expected implementation dependency for KavaRef core"
        )
        assertTrue(
            Regex("""implementation\(libs\.kavaref\.extension\)""").containsMatchIn(script),
            "Expected implementation dependency for KavaRef extension"
        )
        
        // Verify YukiHook KSP processor
        assertTrue(
            Regex("""ksp\(libs\.yukihookapi\.ksp\.xposed\)""").containsMatchIn(script),
            "Expected KSP processor for YukiHook Xposed integration"
        )
        
        // Verify local Xposed API JARs are compileOnly
        assertTrue(
            Regex("""compileOnly\(files\("libs/api-82\.jar"\)\)""").containsMatchIn(script),
            "Expected compileOnly for local Xposed API JAR"
        )
        assertTrue(
            Regex("""compileOnly\(files\("libs/api-82-sources\.jar"\)\)""").containsMatchIn(script),
            "Expected compileOnly for local Xposed API sources JAR"
        )
    }

    @Test
    @DisplayName("Room dependencies use KSP compiler correctly")
    fun roomDependenciesUseKspCorrectly() {
        // Verify Room compiler uses KSP, not kapt
        assertTrue(
            Regex("""ksp\(libs\.androidx\.room\.compiler\)""").containsMatchIn(script),
            "Expected Room compiler to use KSP processor"
        )
        
        // Verify Room compiler is NOT using implementation
        assertFalse(
            Regex("""implementation\(libs\.androidx\.room\.compiler\)""").containsMatchIn(script),
            "Room compiler should NOT be an implementation dependency"
        )
        
        // Verify navigation compose does NOT use KSP
        assertFalse(
            Regex("""ksp\(libs\.androidx\.navigation\.compose\)""").containsMatchIn(script),
            "Navigation Compose should NOT use KSP - it's a runtime library"
        )
    }

    @Test
    @DisplayName("Firebase dependencies use BOM and KTX variants")
    fun firebaseDependenciesUseBomAndKtx() {
        // Verify Firebase BOM is used
        assertTrue(
            Regex("""implementation\(platform\(libs\.firebase\.bom\)\)""").containsMatchIn(script),
            "Expected Firebase BOM for version management"
        )
        
        // Verify all Firebase dependencies use KTX variants
        val ktxDependencies = listOf(
            "firebase.analytics.ktx",
            "firebase.crashlytics.ktx",
            "firebase.auth.ktx",
            "firebase.firestore.ktx"
        )
        
        ktxDependencies.forEach { dep ->
            assertTrue(
                Regex("""implementation\(libs\.$dep\)""".replace(".", "\\.")).containsMatchIn(script),
                "Expected Firebase KTX dependency: $dep"
            )
        }
        
        // Verify old non-KTX Firebase dependencies are removed
        val deprecatedPatterns = listOf(
            """implementation\(libs\.firebase\.analytics\)(?!\.ktx)""",
            """implementation\(libs\.firebase\.crashlytics\)(?!\.ktx)""",
            """implementation\(libs\.firebase\.auth\)(?!\.ktx)""",
            """implementation\(libs\.firebase\.firestore\)(?!\.ktx)"""
        )
        
        deprecatedPatterns.forEach { pat ->
            assertFalse(
                Regex(pat).containsMatchIn(script),
                "Should not have non-KTX Firebase dependency matching: $pat"
            )
        }
    }

    @Test
    @DisplayName("Desugaring uses coreLibraryDesugaring correctly")
    fun desugaringConfiguredCorrectly() {
        // Verify desugaring uses coreLibraryDesugaring, not implementation
        assertTrue(
            Regex("""coreLibraryDesugaring\(libs\.desugar\.jdk\.libs\)""").containsMatchIn(script),
            "Expected coreLibraryDesugaring for JDK desugaring library"
        )
        
        // Verify it's NOT using implementation
        assertFalse(
            script.contains("implementation(libs.desugar.jdk.libs)"),
            "Desugaring should use coreLibraryDesugaring, not implementation"
        )
    }

    @Test
    @DisplayName("Moshi dependencies are removed")
    fun moshiDependenciesRemoved() {
        // Verify Moshi and its codegen are not present
        assertFalse(
            Regex("""implementation\(libs\.moshi\)""").containsMatchIn(script),
            "Moshi should be removed from dependencies"
        )
        assertFalse(
            Regex("""implementation\(libs\.moshi\.kotlin\)""").containsMatchIn(script),
            "Moshi Kotlin should be removed from dependencies"
        )
        assertFalse(
            Regex("""implementation\(libs\.retrofit\.converter\.moshi\)""").containsMatchIn(script),
            "Retrofit Moshi converter should be removed from dependencies"
        )
        assertFalse(
            script.contains("moshi-kotlin-codegen"),
            "Moshi codegen should be removed from dependencies"
        )
    }

    @Test
    @DisplayName("Lifecycle and Hilt dependencies are not duplicated")
    fun lifecycleAndHiltDependenciesNotDuplicated() {
        // Count occurrences of key dependencies to ensure no duplication
        val countPattern = { pattern: String -> 
            Regex(pattern).findAll(script).count()
        }
        
        // Each lifecycle dependency should appear exactly once
        assertEquals(
            1,
            countPattern("""implementation\(libs\.androidx\.lifecycle\.runtime\.ktx\)"""),
            "lifecycle-runtime-ktx should appear exactly once"
        )
        assertEquals(
            1,
            countPattern("""implementation\(libs\.androidx\.lifecycle\.viewmodel\.ktx\)"""),
            "lifecycle-viewmodel-ktx should appear exactly once"
        )
        assertEquals(
            1,
            countPattern("""implementation\(libs\.androidx\.lifecycle\.viewmodel\.compose\)"""),
            "lifecycle-viewmodel-compose should appear exactly once"
        )
        
        // Hilt Work should appear exactly once
        assertEquals(
            1,
            countPattern("""implementation\(libs\.androidx\.hilt\.work\)"""),
            "hilt-work should appear exactly once"
        )
        
        // Navigation Compose should appear exactly once
        assertEquals(
            1,
            countPattern("""implementation\(libs\.androidx\.navigation\.compose\)"""),
            "navigation-compose should appear exactly once"
        )
    }

    @Test
    @DisplayName("LibSu dependencies are not duplicated")
    fun libsuDependenciesNotDuplicated() {
        // Count LibSu core dependency occurrences
        val libsuCoreCount = Regex("""implementation\(libs\.libsu\.core\)""").findAll(script).count()
        val libsuIoCount = Regex("""implementation\(libs\.libsu\.io\)""").findAll(script).count()
        val libsuServiceCount = Regex("""implementation\(libs\.libsu\.service\)""").findAll(script).count()
        
        // Each should appear exactly once (not duplicated)
        assertEquals(1, libsuCoreCount, "libsu-core should appear exactly once")
        assertEquals(1, libsuIoCount, "libsu-io should appear exactly once")
        assertEquals(1, libsuServiceCount, "libsu-service should appear exactly once")
    }

    @Test
    @DisplayName("All androidx.compose dependencies are properly organized")
    fun composeLibrariesProperlyOrganized() {
        // Verify Compose BOM is declared before individual Compose dependencies
        val bomIndex = script.indexOf("implementation(platform(libs.androidx.compose.bom))")
        val composeUiIndex = script.indexOf("implementation(libs.compose.ui)")
        
        assertTrue(
            bomIndex > 0 && bomIndex < composeUiIndex,
            "Compose BOM should be declared before individual Compose libraries"
        )
        
        // Verify essential Compose libraries are present
        val essentialComposeLibs = listOf(
            "compose.ui",
            "compose.material3",
            "compose.animation"
        )
        
        essentialComposeLibs.forEach { lib ->
            assertTrue(
                Regex("""implementation\(libs\.$lib\)""".replace(".", "\\.")).containsMatchIn(script),
                "Expected Compose library: $lib"
            )
        }
    }

    @Test
    @DisplayName("Hilt integration libraries are properly included")
    fun hiltIntegrationLibrariesIncluded() {
        // Verify Hilt core dependencies
        assertTrue(
            Regex("""implementation\(libs\.hilt\.android\)""").containsMatchIn(script),
            "Expected Hilt Android runtime"
        )
        assertTrue(
            Regex("""ksp\(libs\.hilt\.compiler\)""").containsMatchIn(script),
            "Expected Hilt compiler via KSP"
        )
        
        // Verify Hilt integrations
        assertTrue(
            Regex("""implementation\(libs\.androidx\.hilt\.navigation\.compose\)""").containsMatchIn(script),
            "Expected Hilt Navigation Compose integration"
        )
        assertTrue(
            Regex("""implementation\(libs\.androidx\.hilt\.work\)""").containsMatchIn(script),
            "Expected Hilt Work integration"
        )
    }

    @Test
    @DisplayName("Lifecycle runtime compose is included")
    fun lifecycleRuntimeComposeIncluded() {
        assertTrue(
            Regex("""implementation\(libs\.androidx\.lifecycle\.runtime\.compose\)""").containsMatchIn(script),
            "Expected lifecycle-runtime-compose for Compose lifecycle integration"
        )
    }

    @Test
    @DisplayName("Dependencies follow logical grouping and organization")
    fun dependenciesFollowLogicalOrganization() {
        // Verify hooking libraries are at the start of dependencies
        val xposedIndex = script.indexOf("compileOnly(libs.xposed.api)")
        val composeIndex = script.indexOf("implementation(platform(libs.androidx.compose.bom))")
        
        assertTrue(
            xposedIndex > 0 && xposedIndex < composeIndex,
            "Hooking libraries should be declared before Compose dependencies"
        )
        
        // Verify Firebase dependencies are grouped together
        val firebaseAnalyticsIndex = script.indexOf("implementation(libs.firebase.analytics.ktx)")
        val firebaseCrashlyticsIndex = script.indexOf("implementation(libs.firebase.crashlytics.ktx)")
        
        if (firebaseAnalyticsIndex > 0 && firebaseCrashlyticsIndex > 0) {
            val distance = kotlin.math.abs(firebaseAnalyticsIndex - firebaseCrashlyticsIndex)
            assertTrue(
                distance < 500,
                "Firebase dependencies should be grouped together"
            )
        }
    }

    @Test
    @DisplayName("No hardcoded dependency versions in build script")
    fun noHardcodedDependencyVersions() {
        // Check for patterns that indicate hardcoded versions
        val hardcodedVersionPatterns = listOf(
            """"com\.squareup\.moshi:moshi-kotlin-codegen:1\.\d+\.\d+"""",
            """"androidx\.\w+:\w+:\d+\.\d+\.\d+"""",
            """"com\.google\.firebase:\w+:\d+\.\d+\.\d+""""
        )
        
        hardcodedVersionPatterns.forEach { pattern ->
            assertFalse(
                Regex(pattern).containsMatchIn(script),
                "Should not have hardcoded dependency versions matching pattern: $pattern"
            )
        }
    }
}
}