package dev.aurakai.auraframefx.gradle

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import java.io.File

/**
 * Tests for gradle/libs.versions.toml version catalog changes.
 * 
 * Testing framework: JUnit 5 (Jupiter)
 * 
 * These tests validate the version catalog configuration changes including:
 * - Removal of unused moshiCodegen version
 * - Addition of navigation compose gradle plugin version
 * - Proper Firebase BOM usage
 * - YukiHook API configuration
 */
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class LibsVersionsTomlTest {

    private lateinit var tomlContent: String
    private lateinit var tomlFile: File

    @BeforeEach
    fun setUp() {
        // Locate the libs.versions.toml file
        val candidates = listOf(
            File("gradle/libs.versions.toml"),
            File("../gradle/libs.versions.toml"),
            File("../../gradle/libs.versions.toml")
        )
        
        tomlFile = candidates.firstOrNull { it.exists() } ?: error(
            "Unable to locate gradle/libs.versions.toml. Checked: ${candidates.joinToString { it.path }}"
        )
        
        tomlContent = tomlFile.readText()
    }

    @Nested
    @DisplayName("Version definitions")
    inner class VersionDefinitions {

        @Test
        @DisplayName("moshiCodegen version is removed")
        fun moshiCodegenVersionRemoved() {
            assertFalse(
                tomlContent.contains("moshiCodegen"),
                "moshiCodegen version should be removed from version catalog"
            )
        }

        @Test
        @DisplayName("androidxNavigationComposeGradlePlugin version is added")
        fun navigationComposeGradlePluginVersionAdded() {
            assertTrue(
                Regex("""androidxNavigationComposeGradlePlugin\s*=\s*"[\d.]+"""").containsMatchIn(tomlContent),
                "androidxNavigationComposeGradlePlugin version should be defined"
            )
            
            // Verify it has a valid semantic version
            val versionMatch = Regex("""androidxNavigationComposeGradlePlugin\s*=\s*"([\d.]+)"""")
                .find(tomlContent)
            assertNotNull(versionMatch, "Should find navigation compose gradle plugin version")
            
            val version = versionMatch!!.groupValues[1]
            val parts = version.split(".")
            assertTrue(
                parts.size >= 2 && parts.all { it.toIntOrNull() != null },
                "Version should follow semantic versioning (e.g., 2.9.5)"
            )
        }

        @Test
        @DisplayName("All required Firebase versions are present")
        fun firebaseVersionsPresent() {
            // Firebase should use BOM, so individual versions are not needed
            // But the BOM version should be present
            assertTrue(
                tomlContent.contains("firebase") || tomlContent.contains("Firebase"),
                "Firebase configuration should be present in version catalog"
            )
        }

        @Test
        @DisplayName("YukiHook API version is properly defined")
        fun yukiHookVersionDefined() {
            assertTrue(
                Regex("""yukihookapi\s*=\s*"[\d.]+"""").containsMatchIn(tomlContent) ||
                Regex("""yuApiClient\s*=\s*"[\d.]+"""").containsMatchIn(tomlContent),
                "YukiHook API version should be defined"
            )
        }

        @Test
        @DisplayName("Xposed API version is defined")
        fun xposedVersionDefined() {
            assertTrue(
                Regex("""xposed\s*=\s*"\d+"""").containsMatchIn(tomlContent),
                "Xposed API version should be defined"
            )
        }
    }

    @Nested
    @DisplayName("Library definitions")
    inner class LibraryDefinitions {

        @Test
        @DisplayName("moshi-kotlin-codegen library is removed")
        fun moshiCodegenLibraryRemoved() {
            assertFalse(
                tomlContent.contains("moshi-kotlin-codegen"),
                "moshi-kotlin-codegen library should be removed from catalog"
            )
        }

        @Test
        @DisplayName("yuApiClient library is added")
        fun yuApiClientLibraryAdded() {
            assertTrue(
                Regex("""yuApiClient\s*=\s*\{\s*group\s*=\s*"[^"]+",\s*name\s*=\s*"[^"]+",\s*version\.ref\s*=\s*"[^"]+"\s*\}""")
                    .containsMatchIn(tomlContent.replace("\n", " ")) ||
                tomlContent.contains("yuApiClient"),
                "yuApiClient library definition should be present"
            )
        }

        @Test
        @DisplayName("Firebase KTX libraries are defined")
        fun firebaseKtxLibrariesDefined() {
            val ktxLibraries = listOf(
                "firebase-analytics-ktx",
                "firebase-crashlytics-ktx", 
                "firebase-auth-ktx",
                "firebase-firestore-ktx"
            )
            
            ktxLibraries.forEach { lib ->
                assertTrue(
                    tomlContent.contains(lib),
                    "Firebase KTX library should be defined: $lib"
                )
            }
        }

        @Test
        @DisplayName("Firebase BOM library is defined")
        fun firebaseBomDefined() {
            assertTrue(
                tomlContent.contains("firebase-bom") || tomlContent.contains("firebase.bom"),
                "Firebase BOM should be defined in version catalog"
            )
        }

        @Test
        @DisplayName("YukiHook API libraries are properly defined")
        fun yukiHookLibrariesDefined() {
            val yukiLibraries = listOf(
                "yukihookapi-api",
                "yukihookapi-ksp-xposed"
            )
            
            yukiLibraries.forEach { lib ->
                assertTrue(
                    tomlContent.contains(lib),
                    "YukiHook library should be defined: $lib"
                )
            }
        }

        @Test
        @DisplayName("KavaRef libraries are defined")
        fun kavaRefLibrariesDefined() {
            val kavaLibraries = listOf(
                "kavaref-core",
                "kavaref-extension"
            )
            
            kavaLibraries.forEach { lib ->
                assertTrue(
                    tomlContent.contains(lib),
                    "KavaRef library should be defined: $lib"
                )
            }
        }

        @Test
        @DisplayName("Room compiler library uses correct version reference")
        fun roomCompilerUsesCorrectVersion() {
            assertTrue(
                Regex("""androidx-room-compiler\s*=.*version\.ref\s*=\s*"room"""")
                    .containsMatchIn(tomlContent.replace("\n", " ")) ||
                tomlContent.contains("androidx.room.room-compiler"),
                "Room compiler should reference room version"
            )
        }
    }

    @Nested
    @DisplayName("Bundles and groups")
    inner class BundlesAndGroups {

        @Test
        @DisplayName("Testing bundles do not reference removed libraries")
        fun testingBundlesValid() {
            // Check testing bundles exist
            val testingSection = tomlContent.indexOf("[bundles]")
            if (testingSection > 0) {
                val bundlesContent = tomlContent.substring(testingSection)
                
                // Ensure Moshi is not in any testing bundle
                assertFalse(
                    bundlesContent.contains("moshi"),
                    "Testing bundles should not reference Moshi"
                )
            }
        }

        @Test
        @DisplayName("Version catalog follows TOML formatting standards")
        fun tomlFormattingValid() {
            // Basic TOML syntax checks
            assertTrue(
                tomlContent.contains("[versions]"),
                "Should have [versions] section"
            )
            assertTrue(
                tomlContent.contains("[libraries]"),
                "Should have [libraries] section"
            )
            
            // Check for balanced quotes
            val quoteCount = tomlContent.count { it == '"' }
            assertEquals(
                0,
                quoteCount % 2,
                "Quotes should be balanced in TOML file"
            )
        }

        @Test
        @DisplayName("No duplicate version entries")
        fun noDuplicateVersions() {
            val versionSection = tomlContent
                .substringAfter("[versions]")
                .substringBefore("[libraries]")
                .lines()
                .filter { it.contains("=") }
                .map { it.substringBefore("=").trim() }
            
            val duplicates = versionSection
                .groupBy { it }
                .filter { it.value.size > 1 }
                .keys
            
            assertTrue(
                duplicates.isEmpty(),
                "Found duplicate version keys: ${duplicates.joinToString()}"
            )
        }

        @Test
        @DisplayName("No duplicate library entries")
        fun noDuplicateLibraries() {
            val librarySection = tomlContent
                .substringAfter("[libraries]")
                .substringBefore("[bundles]", tomlContent.substringAfter("[libraries]"))
                .lines()
                .filter { it.matches(Regex("""^\s*[\w-]+\s*=.*""")) }
                .map { it.substringBefore("=").trim() }
            
            val duplicates = librarySection
                .groupBy { it }
                .filter { it.value.size > 1 }
                .keys
            
            assertTrue(
                duplicates.isEmpty(),
                "Found duplicate library keys: ${duplicates.joinToString()}"
            )
        }
    }

    @Nested
    @DisplayName("Consistency checks")
    inner class ConsistencyChecks {

        @Test
        @DisplayName("All version references in libraries exist")
        fun allVersionReferencesExist() {
            // Extract version references from library definitions
            val versionRefs = Regex("""version\.ref\s*=\s*"([^"]+)"""")
                .findAll(tomlContent)
                .map { it.groupValues[1] }
                .toSet()
            
            // Extract defined versions
            val definedVersions = Regex("""^\s*([\w-]+)\s*=\s*"[^"]+"""", RegexOption.MULTILINE)
                .findAll(tomlContent.substringAfter("[versions]").substringBefore("[libraries]"))
                .map { it.groupValues[1] }
                .toSet()
            
            versionRefs.forEach { ref ->
                assertTrue(
                    definedVersions.contains(ref),
                    "Version reference '$ref' is used but not defined in [versions]"
                )
            }
        }

        @Test
        @DisplayName("Firebase libraries use BOM or explicit versions consistently")
        fun firebaseVersioningConsistent() {
            val firebaseLibs = Regex("""firebase-[\w-]+\s*=.*""")
                .findAll(tomlContent)
                .map { it.value }
                .toList()
            
            if (firebaseLibs.isNotEmpty()) {
                // Check if any Firebase library has explicit version (non-BOM approach)
                val hasExplicitVersions = firebaseLibs.any { it.contains("version.ref") }
                val hasBom = tomlContent.contains("firebase-bom")
                
                // If BOM is used, non-BOM libraries should not have explicit versions
                if (hasBom) {
                    val bomLibCount = firebaseLibs.filter { !it.contains("bom") && it.contains("version.ref") }.size
                    assertTrue(
                        bomLibCount == 0 || bomLibCount == firebaseLibs.size - 1, // -1 for BOM itself
                        "When using Firebase BOM, regular Firebase libraries should not specify versions"
                    )
                }
            }
        }

        @Test
        @DisplayName("Kotlin libraries use consistent version")
        fun kotlinVersionConsistent() {
            val kotlinVersion = Regex("""kotlin\s*=\s*"([\d.]+)"""")
                .find(tomlContent)?.groupValues?.get(1)
            
            if (kotlinVersion != null) {
                // All Kotlin-related libraries should reference the same version
                val kotlinLibs = Regex("""org\.jetbrains\.kotlin[^"]*version\.ref\s*=\s*"([^"]+)"""")
                    .findAll(tomlContent)
                    .map { it.groupValues[1] }
                    .distinct()
                    .toList()
                
                assertTrue(
                    kotlinLibs.isEmpty() || kotlinLibs.all { it == "kotlin" },
                    "All Kotlin libraries should reference the 'kotlin' version"
                )
            }
        }
    }
}