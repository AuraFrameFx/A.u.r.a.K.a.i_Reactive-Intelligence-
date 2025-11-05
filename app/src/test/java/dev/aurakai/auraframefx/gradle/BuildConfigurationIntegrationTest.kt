package dev.aurakai.auraframefx.gradle

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import java.io.File

/**
 * Integration tests for build configuration consistency across the project.
 * 
 * Testing framework: JUnit 5 (Jupiter)
 * 
 * These tests ensure consistency between:
 * - app/build.gradle.kts dependencies and gradle/libs.versions.toml definitions
 * - Version references and actual versions
 * - Dependency declarations across modules
 */
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class BuildConfigurationIntegrationTest {

    private lateinit var appBuildScript: String
    private lateinit var libsVersionsToml: String

    @BeforeEach
    fun setUp() {
        // Load app build script
        val appBuildCandidates = listOf(
            File("app/build.gradle.kts"),
            File("../app/build.gradle.kts")
        )
        val appBuildFile = appBuildCandidates.firstOrNull { it.exists() } 
            ?: error("Unable to locate app/build.gradle.kts")
        appBuildScript = appBuildFile.readText()

        // Load libs.versions.toml
        val tomlCandidates = listOf(
            File("gradle/libs.versions.toml"),
            File("../gradle/libs.versions.toml")
        )
        val tomlFile = tomlCandidates.firstOrNull { it.exists() }
            ?: error("Unable to locate gradle/libs.versions.toml")
        libsVersionsToml = tomlFile.readText()
    }

    @Nested
    @DisplayName("Dependency reference consistency")
    inner class DependencyReferenceConsistency {

        @Test
        @DisplayName("All libs references in app build script exist in version catalog")
        fun allLibsReferencesExistInCatalog() {
            // Extract all libs.* references from app build script
            val libsRefs = Regex("""libs\.([\w.]+)""")
                .findAll(appBuildScript)
                .map { it.groupValues[1] }
                .distinct()
                .toList()

            val failures = mutableListOf<String>()
            
            libsRefs.forEach { ref ->
                // Convert from dot notation to hyphen notation for search
                val searchRef = ref.replace(".", "-")
                
                if (!libsVersionsToml.contains(searchRef) && !libsVersionsToml.contains(ref)) {
                    failures.add(ref)
                }
            }

            assertTrue(
                failures.isEmpty(),
                "Found ${failures.size} lib references not defined in version catalog: ${failures.joinToString()}"
            )
        }

        @Test
        @DisplayName("Firebase dependencies use BOM in app build script")
        fun firebaseDependenciesUseBom() {
            val firebaseDeps = Regex("""implementation\(libs\.firebase\.[\w.]+\)""")
                .findAll(appBuildScript)
                .count()

            if (firebaseDeps > 0) {
                assertTrue(
                    appBuildScript.contains("implementation(platform(libs.firebase.bom))"),
                    "When using Firebase dependencies, BOM should be included"
                )
            }
        }

        @Test
        @DisplayName("Compose dependencies use BOM in app build script")
        fun composeDependenciesUseBom() {
            val composeDeps = Regex("""implementation\(libs\.compose\.[\w.]+\)""")
                .findAll(appBuildScript)
                .count()

            if (composeDeps > 0) {
                assertTrue(
                    appBuildScript.contains("implementation(platform(libs.androidx.compose.bom))"),
                    "When using Compose dependencies, BOM should be included"
                )
            }
        }

        @Test
        @DisplayName("KSP dependencies match processor types")
        fun kspDependenciesMatchProcessorTypes() {
            // Extract all KSP dependencies
            val kspDeps = Regex("""ksp\(libs\.([\w.]+)\)""")
                .findAll(appBuildScript)
                .map { it.groupValues[1] }
                .toList()

            // Known processors that should use KSP
            val validKspProcessors = listOf(
                "hilt.compiler",
                "androidx.room.compiler", 
                "room.compiler",
                "yukihookapi.ksp.xposed"
            )

            kspDeps.forEach { dep ->
                val matchesValidProcessor = validKspProcessors.any { 
                    dep.contains(it.replace(".", ".")) 
                }
                assertTrue(
                    matchesValidProcessor || dep.contains("compiler") || dep.contains("ksp"),
                    "KSP dependency should be a processor: $dep"
                )
            }
        }
    }

    @Nested
    @DisplayName("Dependency organization")
    inner class DependencyOrganization {

        @Test
        @DisplayName("Hooking dependencies are declared before app dependencies")
        fun hookingDependenciesFirst() {
            val xposedIndex = appBuildScript.indexOf("compileOnly(libs.xposed.api)")
            val composeIndex = appBuildScript.indexOf("implementation(platform(libs.androidx.compose.bom))")

            if (xposedIndex > 0 && composeIndex > 0) {
                assertTrue(
                    xposedIndex < composeIndex,
                    "Hooking/Xposed dependencies should be declared before app framework dependencies"
                )
            }
        }

        @Test
        @DisplayName("BOMs are declared before their dependent libraries")
        fun bomsBeforeDependentLibraries() {
            // Check Compose BOM ordering
            val composeBomIndex = appBuildScript.indexOf("implementation(platform(libs.androidx.compose.bom))")
            val composeUiIndex = appBuildScript.indexOf("implementation(libs.compose.ui)")

            if (composeBomIndex > 0 && composeUiIndex > 0) {
                assertTrue(
                    composeBomIndex < composeUiIndex,
                    "Compose BOM should be declared before Compose UI dependencies"
                )
            }

            // Check Firebase BOM ordering
            val firebaseBomIndex = appBuildScript.indexOf("implementation(platform(libs.firebase.bom))")
            val firebaseAnalyticsIndex = appBuildScript.indexOf("implementation(libs.firebase.analytics")

            if (firebaseBomIndex > 0 && firebaseAnalyticsIndex > 0) {
                assertTrue(
                    firebaseBomIndex < firebaseAnalyticsIndex,
                    "Firebase BOM should be declared before Firebase dependencies"
                )
            }
        }

        @Test
        @DisplayName("Desugaring uses correct configuration")
        fun desugaringUsesCorrectConfiguration() {
            val desugaringDeclarations = Regex("""(implementation|coreLibraryDesugaring)\(libs\.desugar\.jdk\.libs\)""")
                .findAll(appBuildScript)
                .toList()

            assertTrue(
                desugaringDeclarations.isNotEmpty(),
                "Desugaring library should be declared"
            )

            val usesCorrectConfig = desugaringDeclarations.any { 
                it.value.contains("coreLibraryDesugaring")
            }
            assertTrue(
                usesCorrectConfig,
                "Desugaring should use coreLibraryDesugaring configuration"
            )
        }
    }

    @Nested
    @DisplayName("Removed dependencies")
    inner class RemovedDependencies {

        @Test
        @DisplayName("Moshi dependencies are completely removed")
        fun moshiDependenciesRemoved() {
            assertFalse(
                appBuildScript.contains("moshi"),
                "Moshi dependencies should be removed from app build script"
            )
            
            assertFalse(
                libsVersionsToml.contains("moshiCodegen"),
                "moshiCodegen version should be removed from version catalog"
            )
        }

        @Test
        @DisplayName("Duplicate dependencies are removed")
        fun duplicateDependenciesRemoved() {
            val dependencyPattern = Regex("""(implementation|api|compileOnly|ksp)\(libs\.([\w.]+)\)""")
            val dependencies = dependencyPattern.findAll(appBuildScript)
                .map { it.groupValues[2] }
                .toList()

            val duplicates = dependencies
                .groupBy { it }
                .filter { it.value.size > 1 }
                .keys

            assertTrue(
                duplicates.isEmpty(),
                "Found duplicate dependency declarations: ${duplicates.joinToString()}"
            )
        }

        @Test
        @DisplayName("Hardcoded dependency versions are removed")
        fun hardcodedVersionsRemoved() {
            val hardcodedPattern = Regex(""""[\w.]+:[\w-]+:\d+\.\d+""")
            val hardcodedDeps = hardcodedPattern.findAll(appBuildScript)
                .map { it.value }
                .filter { !it.contains("junit.platform") } // Exception for test platform
                .toList()

            assertTrue(
                hardcodedDeps.isEmpty(),
                "Found hardcoded dependency versions: ${hardcodedDeps.joinToString()}"
            )
        }
    }

    @Nested
    @DisplayName("Best practices")
    inner class BestPractices {

        @Test
        @DisplayName("Firebase uses KTX variants")
        fun firebaseUsesKtxVariants() {
            val firebaseNonKtx = Regex("""implementation\(libs\.firebase\.(analytics|crashlytics|auth|firestore)\)""")
                .findAll(appBuildScript)
                .filter { !it.value.contains("ktx") }
                .toList()

            assertTrue(
                firebaseNonKtx.isEmpty(),
                "Firebase dependencies should use KTX variants: ${firebaseNonKtx.map { it.value }}"
            )
        }

        @Test
        @DisplayName("Room compiler uses KSP not kapt")
        fun roomCompilerUsesKsp() {
            if (appBuildScript.contains("room.compiler")) {
                assertTrue(
                    appBuildScript.contains("ksp(libs.androidx.room.compiler)") ||
                    appBuildScript.contains("ksp(libs.room.compiler)"),
                    "Room compiler should use KSP processor"
                )
                
                assertFalse(
                    appBuildScript.contains("kapt(libs.androidx.room.compiler)"),
                    "Room compiler should not use kapt"
                )
            }
        }

        @Test
        @DisplayName("Xposed API is compileOnly not implementation")
        fun xposedApiIsCompileOnly() {
            if (appBuildScript.contains("xposed.api")) {
                assertTrue(
                    appBuildScript.contains("compileOnly(libs.xposed.api)"),
                    "Xposed API should be compileOnly"
                )
                
                assertFalse(
                    appBuildScript.contains("implementation(libs.xposed.api)"),
                    "Xposed API should not be implementation dependency"
                )
            }
        }

        @Test
        @DisplayName("Test dependencies use testImplementation")
        fun testDependenciesUseCorrectConfiguration() {
            val testLibs = listOf("junit", "mockk", "junit.jupiter")
            
            testLibs.forEach { lib ->
                val implPattern = Regex("""implementation\(libs\.$lib[).]""")
                val hasImplDeclaration = implPattern.containsMatchIn(appBuildScript)
                
                if (hasImplDeclaration) {
                    fail("Test library should use testImplementation, not implementation: $lib")
                }
            }
        }
    }
}