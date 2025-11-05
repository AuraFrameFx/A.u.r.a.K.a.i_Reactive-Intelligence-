package dev.aurakai.auraframefx.gradle

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import java.io.File

/**
 * Tests for core/ui/build.gradle.kts build script.
 * 
 * Testing framework: JUnit 5 (Jupiter)
 * 
 * These tests validate the core UI module build configuration including:
 * - Proper code formatting and whitespace
 * - Essential dependencies
 * - Build configuration consistency
 */
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class CoreUiBuildScriptTest {

    private lateinit var buildScript: String
    private lateinit var buildFile: File

    @BeforeEach
    fun setUp() {
        val candidates = listOf(
            File("core/ui/build.gradle.kts"),
            File("../core/ui/build.gradle.kts"),
            File("../../core/ui/build.gradle.kts")
        )
        
        buildFile = candidates.firstOrNull { it.exists() } ?: error(
            "Unable to locate core/ui/build.gradle.kts. Checked: ${candidates.joinToString { it.path }}"
        )
        
        buildScript = buildFile.readText()
    }

    @Test
    @DisplayName("Build script has no trailing empty lines after closing brace")
    fun noTrailingEmptyLines() {
        val lines = buildScript.lines()
        val lastNonEmptyIndex = lines.indexOfLast { it.isNotBlank() }
        
        assertTrue(
            lastNonEmptyIndex == lines.lastIndex || lastNonEmptyIndex == lines.lastIndex - 1,
            "Build script should not have excessive trailing empty lines"
        )
    }

    @Test
    @DisplayName("Dependencies block is properly formatted")
    fun dependenciesBlockFormatted() {
        assertTrue(
            buildScript.contains("dependencies {"),
            "Should have dependencies block"
        )
        
        // Check that dependencies block doesn't have extra blank lines before closing brace
        val dependenciesSection = buildScript.substringAfter("dependencies {")
        if (dependenciesSection.contains("}")) {
            val beforeClosing = dependenciesSection.substringBefore("}")
            val trailingBlankLines = beforeClosing.lines().reversed()
                .takeWhile { it.isBlank() }
                .count()
            
            assertTrue(
                trailingBlankLines <= 1,
                "Dependencies block should not have excessive blank lines before closing brace"
            )
        }
    }

    @Test
    @DisplayName("Essential UI dependencies are present")
    fun essentialUiDependenciesPresent() {
        val essentialDeps = listOf(
            "androidx.core.ktx",
            "androidx.appcompat"
        )
        
        essentialDeps.forEach { dep ->
            assertTrue(
                Regex("""(api|implementation)\(libs\.$dep\)""".replace(".", "\\.")).containsMatchIn(buildScript),
                "Essential UI dependency should be present: $dep"
            )
        }
    }

    @Test
    @DisplayName("Android configuration is properly set up")
    fun androidConfigurationSetUp() {
        assertTrue(
            buildScript.contains("android {"),
            "Should have android configuration block"
        )
        
        assertTrue(
            buildScript.contains("namespace"),
            "Should have namespace configured"
        )
    }

    @Test
    @DisplayName("No syntax errors in Kotlin DSL")
    fun noKotlinDslSyntaxErrors() {
        // Check for balanced braces
        val openBraces = buildScript.count { it == '{' }
        val closeBraces = buildScript.count { it == '}' }
        
        assertEquals(
            openBraces,
            closeBraces,
            "Braces should be balanced in Kotlin DSL"
        )
        
        // Check for balanced parentheses
        val openParens = buildScript.count { it == '(' }
        val closeParens = buildScript.count { it == ')' }
        
        assertEquals(
            openParens,
            closeParens,
            "Parentheses should be balanced in Kotlin DSL"
        )
    }

    @Test
    @DisplayName("File uses consistent indentation")
    fun consistentIndentation() {
        val lines = buildScript.lines().filter { it.isNotBlank() }
        val indentedLines = lines.filter { it.startsWith(" ") || it.startsWith("\t") }
        
        if (indentedLines.isNotEmpty()) {
            // Check if using spaces or tabs consistently
            val usesSpaces = indentedLines.any { it.startsWith(" ") }
            val usesTabs = indentedLines.any { it.startsWith("\t") }
            
            assertFalse(
                usesSpaces && usesTabs,
                "Should use either spaces or tabs consistently, not both"
            )
        }
    }

    @Test
    @DisplayName("No commented out code blocks")
    fun noCommentedOutCodeBlocks() {
        // Check for patterns that indicate commented out dependency declarations
        val commentedDepPattern = Regex("""//\s*(implementation|api|compileOnly|testImplementation)\(""")
        val matches = commentedDepPattern.findAll(buildScript).count()
        
        // Allow a small number of commented examples, but flag excessive commenting
        assertTrue(
            matches <= 2,
            "Found $matches commented out dependency declarations - consider removing dead code"
        )
    }

    @Test
    @DisplayName("Module references use project() syntax")
    fun moduleReferencesUseProjectSyntax() {
        // If there are project dependencies, they should use proper syntax
        val projectDeps = Regex("""(implementation|api)\(project\([^)]+\)\)""")
            .findAll(buildScript)
            .toList()
        
        projectDeps.forEach { match ->
            val depDeclaration = match.value
            assertTrue(
                depDeclaration.contains("\":") || depDeclaration.contains("':"),
                "Project dependencies should use proper string syntax: $depDeclaration"
            )
        }
    }
}