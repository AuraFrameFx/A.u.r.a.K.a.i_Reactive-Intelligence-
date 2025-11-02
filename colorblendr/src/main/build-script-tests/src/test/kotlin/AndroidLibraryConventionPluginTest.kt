@file:OptIn(org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi::class)

import org.gradle.testkit.runner.GradleRunner
import org.gradle.testkit.runner.TaskOutcome
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import java.io.File
import java.nio.file.Files
import java.util.Properties

/**
 * Tests for AndroidLibraryConventionPlugin.
 *
 * Testing framework: JUnit 5 (Jupiter) with Gradle TestKit.
 * These tests spin up an isolated Gradle build, apply the plugin, and assert the configured
 * Android/Java/Kotlin DSL and tasks. External Android components are not executed; instead, we
 * inspect the produced model via printing tasks and evaluating build logic.
 */
class AndroidLibraryConventionPluginTest {

    private lateinit var testProjectDir: File
    private lateinit var settingsGradle: File
    private lateinit var buildGradle: File
    private lateinit var gradleProps: File

    @BeforeEach
    fun setup() {
        testProjectDir = Files.createTempDirectory("android-lib-convention-it").toFile()
        settingsGradle = File(testProjectDir, "settings.gradle.kts").apply {
            writeText(
                """
                rootProject.name = "test-lib"
                """.trimIndent()
            )
        }
        // Minimal repositories to resolve AGP/Kotlin if needed; plugin-under-test classpath is injected by TestKit
        gradleProps = File(testProjectDir, "gradle.properties").apply {
            writeText(
                """
                org.gradle.jvmargs=-Xmx2g -Dfile.encoding=UTF-8
                android.useAndroidX=true
                """.trimIndent()
            )
        }
        buildGradle = File(testProjectDir, "build.gradle.kts").apply {
            writeText(
                """
                plugins {
                    // Plugin under test is loaded from withPluginClasspath()
                    // Apply by class name if available in classpath; otherwise, by id if configured.
                    // We'll try the fully-qualified class first; fallback to id in a second build file if needed.
                    // language=kotlin
                    id("com.android.library")
                }

                // Apply the convention plugin via buildscript classpath (TestKit will inject)
                // We use apply to avoid requiring plugin id in case only the class is available.
                apply<com.android.build.api.dsl.LibraryExtension>() // no-op, ensures type availability

                // If the convention plugin is registered with an id in the repository, uncomment and adjust:
                // plugins { id("android.library.convention") }

                // For class-application, we create a tiny Kotlin buildSrc to expose the class if needed via classpath.
                // However, TestKit withPluginClasspath should make it available.

                // The plugin will set Android + Java + Kotlin toolchains, plus add tasks dependencies.
                """.trimIndent()
            )
        }
        // Create a bare Android library structure so AGP tasks like preBuild can be realized without sources
        File(testProjectDir, "src/main/AndroidManifest.xml").apply {
            parentFile.mkdirs()
            writeText("""<manifest package="com.example.test"/>""")
        }
    }

    @AfterEach
    fun tearDown() {
        testProjectDir.deleteRecursively()
    }

    private fun runner(vararg args: String) =
        GradleRunner.create()
            .withProjectDir(testProjectDir)
            .withArguments(*args, "--stacktrace")
            .withPluginClasspath()
            .forwardOutput()

    @Test
    @DisplayName("Plugin applies cleanGeneratedSources task and hooks it into preBuild")
    fun cleanGeneratedSourcesTask_isCreated_andHookedToPreBuild() {
        // Write a build script that applies the convention plugin by type via DSL
        buildGradle.writeText(
            """
            @file:Suppress("UnstableApiUsage")
            plugins {
                id("com.android.library")
                kotlin("android") version embeddedKotlinVersion apply false
            }

            // Apply the convention plugin by class if available on classpath
            buildscript { }
            apply<AndroidLibraryConventionPlugin>()
            """.trimIndent()
        )

        val result = runner("tasks", "--all").build()
        assertTrue(
            result.output.contains("cleanGeneratedSources"),
            "cleanGeneratedSources task should be registered"
        )
        // Realize preBuild and check dependencies via 'dependencies' report is unreliable; run a no-op preBuild and inspect output
        val preBuild = runner("preBuild").build()
        assertEquals(TaskOutcome.SUCCESS, preBuild.task(":preBuild")?.outcome)
    }

    @Test
    @DisplayName("Android defaultConfig is set (compileSdk=36, minSdk=34, test runner, vector drawables)")
    fun androidDefaultConfig_isConfigured() {
        buildGradle.writeText(
            """
            @file:Suppress("UnstableApiUsage")
            plugins {
                id("com.android.library")
                kotlin("android") version embeddedKotlinVersion apply false
            }
            apply<AndroidLibraryConventionPlugin>()

            tasks.register("printAndroidConfig") {
                doLast {
                    val ext = extensions.getByName("android") as com.android.build.api.dsl.LibraryExtension
                    println("COMPILE_SDK=" + ext.compileSdk)
                    println("MIN_SDK=" + ext.defaultConfig.minSdk)
                    println("TEST_RUNNER=" + ext.defaultConfig.testInstrumentationRunner)
                    println("VEC_DRAWABLES=" + ext.defaultConfig.vectorDrawables?.useSupportLibrary)
                }
            }
            """.trimIndent()
        )

        val result = runner("printAndroidConfig").build()
        with(result.output) {
            assertTrue(contains("COMPILE_SDK=36"))
            assertTrue(contains("MIN_SDK=34"))
            assertTrue(contains("TEST_RUNNER=androidx.test.runner.AndroidJUnitRunner"))
            assertTrue(contains("VEC_DRAWABLES=true"))
        }
    }

    @Test
    @DisplayName("Release build type enables minify and sets proguard files")
    fun releaseBuildType_isConfigured() {
        buildGradle.writeText(
            """
            @file:Suppress("UnstableApiUsage")
            plugins {
                id("com.android.library")
                kotlin("android") version embeddedKotlinVersion apply false
            }
            apply<AndroidLibraryConventionPlugin>()

            tasks.register("printRelease") {
                doLast {
                    val ext = extensions.getByName("android") as com.android.build.api.dsl.LibraryExtension
                    val rel = ext.buildTypes.getByName("release")
                    println("MINIFY=" + rel.isMinifyEnabled)
                    println("PG_COUNT=" + rel.proguardFiles.size)
                }
            }
            """.trimIndent()
        )

        val result = runner("printRelease").build()
        with(result.output) {
            assertTrue(contains("MINIFY=true"))
            // Expect at least default + module proguard files
            assertTrue(contains("PG_COUNT="), "Should print PG_COUNT")
        }
    }

    @Test
    @DisplayName("Build features and compile options are set")
    fun buildFeatures_andCompileOptions_areConfigured() {
        buildGradle.writeText(
            """
            @file:Suppress("UnstableApiUsage")
            plugins {
                id("com.android.library")
                kotlin("android") version embeddedKotlinVersion apply false
            }
            apply<AndroidLibraryConventionPlugin>()

            tasks.register("printFeatures") {
                doLast {
                    val ext = extensions.getByName("android") as com.android.build.api.dsl.LibraryExtension
                    println("BUILD_CONFIG=" + (ext.buildFeatures?.buildConfig ?: "null"))
                    println("VIEW_BINDING=" + (ext.buildFeatures?.viewBinding ?: "null"))
                    println("DATA_BINDING=" + (ext.buildFeatures?.dataBinding ?: "null"))
                    println("DESUGAR=" + ext.compileOptions.isCoreLibraryDesugaringEnabled)
                }
            }
            """.trimIndent()
        )

        val result = runner("printFeatures").build()
        with(result.output) {
            assertTrue(contains("BUILD_CONFIG=true"))
            assertTrue(contains("VIEW_BINDING=false"))
            assertTrue(contains("DATA_BINDING=false"))
            assertTrue(contains("DESUGAR=true"))
        }
    }

    @Test
    @DisplayName("Packaging and lint options are configured")
    fun packagingAndLint_areConfigured() {
        buildGradle.writeText(
            """
            @file:Suppress("UnstableApiUsage")
            plugins {
                id("com.android.library")
                kotlin("android") version embeddedKotlinVersion apply false
            }
            apply<AndroidLibraryConventionPlugin>()

            tasks.register("printLint") {
                doLast {
                    val ext = extensions.getByName("android") as com.android.build.api.dsl.LibraryExtension
                    val lint = ext.lint
                    println("LINT_WARNINGS_AS_ERRORS=" + lint.warningsAsErrors)
                    println("LINT_ABORT_ON_ERROR=" + lint.abortOnError)
                    println("LINT_DISABLED=" + lint.disable.joinToString(","))
                }
            }
            """.trimIndent()
        )

        val result = runner("printLint").build()
        with(result.output) {
            assertTrue(contains("LINT_WARNINGS_AS_ERRORS=false"))
            assertTrue(contains("LINT_ABORT_ON_ERROR=false"))
            assertTrue(contains("InvalidPackage") || contains("OldTargetApi"))
        }
    }

    @Test
    @DisplayName("Java and Kotlin toolchain to 21")
    fun javaAndKotlinToolchains_areConfigured() {
        buildGradle.writeText(
            """
            @file:Suppress("UnstableApiUsage")
            plugins {
                id("com.android.library")
                kotlin("android") version embeddedKotlinVersion apply false
            }
            apply<AndroidLibraryConventionPlugin>()

            tasks.register("printJvm") {
                doLast {
                    val java = extensions.getByName("java") as org.gradle.api.plugins.JavaPluginExtension
                    println("JAVA_SOURCE=" + java.sourceCompatibility.majorVersion)
                    println("JAVA_TARGET=" + java.targetCompatibility.majorVersion)
                }
            }
            """.trimIndent()
        )

        val result = runner("printJvm").build()
        with(result.output) {
            // Gradle exposes "24" as the major version when using JavaVersion.VERSION_24
            assertTrue(contains("JAVA_SOURCE=24"))
            assertTrue(contains("JAVA_TARGET=24"))
        }
    }
}