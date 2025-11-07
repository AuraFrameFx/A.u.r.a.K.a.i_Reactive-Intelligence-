// BuildScriptsValidationTest.kt
// Framework: JUnit Jupiter (JUnit 5) with org.junit.jupiter.api.Assertions; no new dependencies.
// Notes:
// - Focused on validating Gradle Kotlin DSL and Version Catalog invariants via pure functions.
// - Since <diff> content was not provided, these tests exercise robust lint-like checks
//   that typically appear in PRs touching build scripts (plugins, repositories, dependency versions).
package buildscript.tests

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.io.TempDir
import java.nio.file.Path
import kotlin.io.path.createDirectories
import kotlin.io.path.exists
import kotlin.io.path.readText
import kotlin.io.path.writeText

private object BuildScriptLints {
    private fun normalizeEol(s: String) = s.replace("\r\n", "\n")

    private fun nonCommentLines(s: String): List<String> {
        val out = mutableListOf<String>()
        var inBlock = false
        for (raw in normalizeEol(s).lines()) {
            var line = raw
            if (!inBlock) {
                val start = line.indexOf("/*")
                if (start >= 0) {
                    val end = line.indexOf("*/", start + 2)
                    if (end >= 0) {
                        // strip one-line block comment
                        line = line.removeRange(start, end + 2)
                    } else {
                        // start block comment; keep content before
                        line = line.substring(0, start)
                        inBlock = true
                    }
                }
                // strip line comments //
                val sl = line.indexOf("//")
                if (sl >= 0) line = line.substring(0, sl)
                // strip TOML comments #
                val hash = line.indexOf("#")
                if (hash >= 0 && line.trimStart().startsWith("#")) line = ""
            } else {
                val end = line.indexOf("*/")
                if (end >= 0) {
                    line = line.substring(end + 2)
                    inBlock = false
                    // after closing, strip any // on remainder
                    val sl = line.indexOf("//")
                    if (sl >= 0) line = line.substring(0, sl)
                } else {
                    line = ""
                }
            }
            if (line.trim().isNotEmpty()) out += line
        }
        return out
    }

    fun findDynamicVersions(script: String): List<String> {
        // flag :+ versions and "latest.*" selectors
        val dyn = mutableListOf<String>()
        val patterns = listOf(":+", "latest.integration", "latest.release", "latest")
        for (line in nonCommentLines(script)) {
            if (patterns.any { it in line }) dyn += line.trim()
        }
        return dyn
    }

    fun findUnstableQualifiers(script: String): List<String> {
        val rx = Regex("""(?i)-(alpha|beta|snapshot|rc\d*|m\d+|preview|dev)\b""")
        return nonCommentLines(script).filter { rx.containsMatchIn(it) }.map { it.trim() }
    }

    fun hasUseJUnitPlatform(script: String): Boolean =
        nonCommentLines(script).any { it.contains("useJUnitPlatform(") }

    fun findForbiddenRepositories(script: String): List<String> {
        val forb = listOf("jcenter(", "mavenLocal(")
        return nonCommentLines(script).filter { l -> forb.any { it in l } }.map { it.trim() }
    }

    fun hasMavenCentral(script: String): Boolean =
        nonCommentLines(script).any { it.contains("mavenCentral(") }

    fun pluginApplied(script: String, snippet: String): Boolean =
        nonCommentLines(script).joinToString("\n").contains(snippet)

    // ===== Version catalog (TOML) helpers =====
    fun tomlSections(toml: String): Set<String> {
        val rx = Regex("""^\s*\[([A-Za-z0-9_.-]+)]\s*$""", RegexOption.MULTILINE)
        return rx.findAll(toml).map { it.groupValues[1] }.toSet()
    }

    fun tomlHasKeys(toml: String, section: String, keys: List<String>): Boolean {
        val norm = normalizeEol(toml)
        val secRx = Regex("""^\s*\[$section]\s*$""", RegexOption.MULTILINE)
        val start = secRx.find(norm) ?: return false
        val fromIdx = start.range.last + 1
        val endIdx = Regex("""^\s*\[[A-Za-z0-9_.-]+]\s*$""", RegexOption.MULTILINE)
            .find(norm, fromIdx)?.range?.first ?: norm.length
        val body = norm.substring(fromIdx, endIdx)
        return keys.all { key ->
            Regex("""^\s*$key\s*=""", RegexOption.MULTILINE).containsMatchIn(
                body
            )
        }
    }
}

@DisplayName("Build script and version catalog validations")
class BuildScriptsValidationTest {

    @Nested
    @DisplayName("Dependency version policy")
    inner class DependencyVersionPolicy {

        @Test
        fun `dynamic plus versions are flagged`() {
            val script = """
                dependencies {
                    implementation("io.ktor:ktor-client:+")
                    testImplementation("org.junit.jupiter:junit-jupiter-api:5.13.4")
                }
            """.trimIndent()
            val issues = BuildScriptLints.findDynamicVersions(script)
            assertTrue(
                issues.any { ":+\"" in it || ":+')" in it },
                "Expected to flag :+ dynamic version"
            )
            assertEquals(1, issues.size, "Only one dynamic version should be flagged")
        }

        @Test
        fun `commented dynamic versions are ignored`() {
            val script = """
                dependencies {
                    // implementation("io.ktor:ktor-client:+")
                    implementation("io.ktor:ktor-client:2.3.12")
                }
            """.trimIndent()
            val issues = BuildScriptLints.findDynamicVersions(script)
            assertTrue(issues.isEmpty(), "Commented dynamic version must be ignored")
        }

        @Test
        fun `unstable qualifiers are detected`() {
            val script = """
                dependencies {
                    implementation("com.squareup.okhttp3:okhttp:5.0.0-alpha.11")
                    testImplementation("org.example:lib:1.0.0-RC1")
                    androidTestImplementation("org.foo:bar:2.0.0-SNAPSHOT")
                }
            """.trimIndent()
            val issues = BuildScriptLints.findUnstableQualifiers(script)
            assertEquals(3, issues.size)
            assertTrue(issues[0].contains("alpha", ignoreCase = true))
            assertTrue(issues[1].contains("RC", ignoreCase = true))
            assertTrue(issues[2].contains("SNAPSHOT", ignoreCase = true))
        }
    }

    @Nested
    @DisplayName("Root build and settings scripts")
    inner class RootBuildAndSettings {

        @Test
        fun `kotlin jvm plugin and junit platform configured`() {
            val build = """
                plugins {
                    kotlin("jvm") version "1.9.24"
                }
                repositories { mavenCentral() }
                tasks.test {
                    useJUnitPlatform()
                }
            """.trimIndent()
            assertTrue(BuildScriptLints.pluginApplied(build, """kotlin("jvm")"""))
            assertTrue(BuildScriptLints.hasMavenCentral(build))
            assertTrue(BuildScriptLints.hasUseJUnitPlatform(build))
        }

        @Test
        fun `forbidden repositories are flagged`() {
            val build = """
                repositories {
                    mavenCentral()
                    // jcenter() should not be used
                    jcenter()
                    mavenLocal()
                }
            """.trimIndent()
            val issues = BuildScriptLints.findForbiddenRepositories(build)
            assertEquals(2, issues.size)
            assertTrue(issues[0].contains("jcenter(") || issues[1].contains("jcenter("))
            assertTrue(issues.any { it.contains("mavenLocal(") })
        }

        @Test
        fun `settings include pluginManagement and dependencyResolutionManagement`() {
            val settings = """
                pluginManagement {
                    repositories { gradlePluginPortal(); mavenCentral() }
                }
                dependencyResolutionManagement {
                    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
                    repositories { mavenCentral() }
                }
                rootProject.name = "sample"
            """.trimIndent()
            val lines = settings.lines()
            assertTrue(lines.any { it.contains("pluginManagement") })
            assertTrue(lines.any { it.contains("dependencyResolutionManagement") })
            assertTrue(lines.any { it.contains("FAIL_ON_PROJECT_REPOS") })
            assertTrue(lines.any { it.contains("rootProject.name") })
        }
    }

    @Nested
    @DisplayName("Version catalog (libs.versions.toml)")
    inner class VersionCatalogValidation {

        @TempDir
        lateinit var tmp: Path

        @Test
        fun `catalog contains required sections and keys (windows line endings tolerated)`() {
            val catalog = tmp.resolve("gradle").resolve("libs.versions.toml")
            catalog.parent.createDirectories()
            val content = """
                [versions]
                kotlin = "1.9.24"
                junitJupiter = "5.13.4"
                mockk = "1.14.5"

                [libraries]
                junit-jupiter = { group = "org.junit.jupiter", name = "junit-jupiter", version.ref = "junitJupiter" }
                mockk = { module = "io.mockk:mockk", version.ref = "mockk" }
            """.trimIndent().replace("\n", "\r\n")
            catalog.writeText(content)

            assertTrue(catalog.exists(), "libs.versions.toml should exist in temp project")
            val txt = catalog.readText()
            val sections = BuildScriptLints.tomlSections(txt)
            assertTrue("versions" in sections, "versions section required")
            assertTrue("libraries" in sections, "libraries section required")
            assertTrue(
                BuildScriptLints.tomlHasKeys(txt, "versions", listOf("kotlin", "junitJupiter")),
                "kotlin and junitJupiter versions must be defined"
            )
        }

        @Test
        fun `missing junitJupiter key is reported`() {
            val catalog = tmp.resolve("gradle").resolve("libs.versions.toml")
            catalog.parent.createDirectories()
            catalog.writeText(
                """
                [versions]
                kotlin = "2.0.0"
                # junitJupiter missing

                [libraries]
                junit-jupiter = { group = "org.junit.jupiter", name = "junit-jupiter", version.ref = "junitJupiter" }
                """.trimIndent()
            )
            val txt = catalog.readText()
            val ok = BuildScriptLints.tomlHasKeys(txt, "versions", listOf("kotlin", "junitJupiter"))
            assertFalse(ok, "Expect failure when junitJupiter is missing")
        }
    }

    @Nested
    @DisplayName("Integration-like smoke checks on ephemeral project")
    inner class EphemeralProjectSmoke {

        @TempDir
        lateinit var tmp: Path

        @Test
        fun `ephemeral root build passes basic policy checks`() {
            val buildFile = tmp.resolve("build.gradle.kts")
            tmp.resolve("gradle").createDirectories()
            buildFile.writeText(
                """
                plugins { kotlin("jvm") version "1.9.24" }
                repositories { mavenCentral() }
                dependencies {
                    testImplementation(platform("org.junit:junit-bom:5.13.4"))
                    testImplementation("org.junit.jupiter:junit-jupiter-api")
                    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine")
                }
                tasks.test { useJUnitPlatform() }
                """.trimIndent()
            )
            val txt = buildFile.readText()
            assertTrue(BuildScriptLints.pluginApplied(txt, """kotlin("jvm")"""))
            assertTrue(BuildScriptLints.hasMavenCentral(txt))
            assertTrue(BuildScriptLints.findDynamicVersions(txt).isEmpty())
            assertTrue(BuildScriptLints.findUnstableQualifiers(txt).isEmpty())
            assertTrue(BuildScriptLints.hasUseJUnitPlatform(txt))
        }
    }
}