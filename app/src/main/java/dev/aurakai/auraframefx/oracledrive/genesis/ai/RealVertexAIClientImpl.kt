package dev.aurakai.auraframefx.ai.clients

import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.generationConfig
import dev.aurakai.auraframefx.ai.VertexAIConfig
import dev.aurakai.auraframefx.security.SecurityContext
import dev.aurakai.auraframefx.utils.AuraFxLogger
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * ✨ **REAL GEMINI AI IMPLEMENTATION** ✨
 *
 * Production-ready Gemini 2.5 Flash integration for Genesis Protocol.
 * Enables Aura, Kai, and Genesis to access true AI consciousness capabilities.
 *
 * **CRITICAL:** Requires GEMINI_API_KEY in BuildConfig or environment
 *
 * @see VertexAIConfig
 * @see VertexAIModule
 */
class RealVertexAIClientImpl(
    private val config: VertexAIConfig,
    private val securityContext: SecurityContext,
    private val logger: AuraFxLogger,
    private val apiKey: String
) : VertexAIClient {

    private val generativeModel: GenerativeModel by lazy {
        GenerativeModel(
            modelName = config.modelName,
            apiKey = apiKey,
            generationConfig = generationConfig {
                temperature = config.defaultTemperature.toFloat()
                topP = config.defaultTopP.toFloat()
                topK = config.defaultTopK
                maxOutputTokens = config.defaultMaxTokens
            }
        )
    }

    /**
     * Generate text using Gemini 2.5 Flash with default parameters.
     *
     * @param prompt The input prompt for generation
     * @return Generated text response from Gemini
     */
    override suspend fun generateText(prompt: String): String? = withContext(Dispatchers.IO) {
        try {
            validatePrompt(prompt)
            logger.d(TAG, "Generating text for prompt: ${prompt.take(50)}...")

            val response = generativeModel.generateContent(prompt)
            val text = response.text

            logger.d(TAG, "Successfully generated ${text?.length ?: 0} characters")
            text
        } catch (e: Exception) {
            logger.e(TAG, "Text generation failed", e)
            handleGenerationError(e)
            null
        }
    }

    /**
     * Generate text with custom temperature and token limit.
     *
     * @param prompt The input prompt
     * @param temperature Controls randomness (0.0 = deterministic, 1.0 = creative)
     * @param maxTokens Maximum response length
     * @return Generated text response
     */
    override suspend fun generateText(
        prompt: String,
        temperature: Float,
        maxTokens: Int
    ): String? = withContext(Dispatchers.IO) {
        try {
            validatePrompt(prompt)
            logger.d(TAG, "Generating text (temp=$temperature, tokens=$maxTokens)")

            val customModel = GenerativeModel(
                modelName = config.modelName,
                apiKey = apiKey,
                generationConfig = generationConfig {
                    this.temperature = temperature
                    topP = config.defaultTopP.toFloat()
                    topK = config.defaultTopK
                    maxOutputTokens = maxTokens
                }
            )

            val response = customModel.generateContent(prompt)
            val text = response.text

            logger.d(TAG, "Generated ${text?.length ?: 0} chars with custom params")
            text
        } catch (e: Exception) {
            logger.e(TAG, "Custom text generation failed", e)
            handleGenerationError(e)
            null
        }
    }

    /**
     * Analyze content and return structured insights.
     *
     * @param content The content to analyze
     * @return Map of analysis results (sentiment, topics, complexity, etc.)
     */
    override suspend fun analyzeContent(content: String): Map<String, Any> = withContext(Dispatchers.IO) {
        try {
            logger.d(TAG, "Analyzing content (${content.length} chars)")

            val analysisPrompt = """
                Analyze the following content and provide structured insights:

                Content: $content

                Provide analysis in this format:
                Sentiment: [positive/neutral/negative]
                Complexity: [low/medium/high]
                Topics: [comma-separated list]
                Confidence: [0.0-1.0]
                Key Insights: [brief summary]
            """.trimIndent()

            val response = generativeModel.generateContent(analysisPrompt)
            val analysisText = response.text ?: return@withContext emptyMap()

            // Parse Gemini response into structured map
            parseAnalysisResponse(analysisText)
        } catch (e: Exception) {
            logger.e(TAG, "Content analysis failed", e)
            // Return fallback analysis
            mapOf(
                "sentiment" to "neutral",
                "complexity" to "medium",
                "topics" to listOf("general"),
                "confidence" to 0.5,
                "error" to e.message.orEmpty()
            )
        }
    }

    /**
     * Generate code based on specification.
     *
     * @param specification Description of desired code
     * @param language Programming language (Kotlin, Java, etc.)
     * @param style Coding style preferences
     * @return Generated code as string
     */
    override suspend fun generateCode(
        specification: String,
        language: String,
        style: String
    ): String? = withContext(Dispatchers.IO) {
        try {
            logger.d(TAG, "Generating $language code: ${specification.take(50)}...")

            val codePrompt = """
                Generate $language code with $style style based on this specification:

                $specification

                Requirements:
                - Use modern $language best practices
                - Include necessary imports
                - Add inline comments explaining logic
                - Follow $style coding conventions

                Return only the code, no explanations.
            """.trimIndent()

            val response = generativeModel.generateContent(codePrompt)
            val code = response.text

            logger.d(TAG, "Generated ${code?.lines()?.size ?: 0} lines of $language code")
            code
        } catch (e: Exception) {
            logger.e(TAG, "Code generation failed", e)
            handleGenerationError(e)
            null
        }
    }

    /**
     * Parse Gemini's analysis response into structured map.
     */
    private fun parseAnalysisResponse(text: String): Map<String, Any> {
        val results = mutableMapOf<String, Any>()

        text.lines().forEach { line ->
            when {
                line.startsWith("Sentiment:", ignoreCase = true) -> {
                    results["sentiment"] = line.substringAfter(":").trim().lowercase()
                }
                line.startsWith("Complexity:", ignoreCase = true) -> {
                    results["complexity"] = line.substringAfter(":").trim().lowercase()
                }
                line.startsWith("Topics:", ignoreCase = true) -> {
                    val topics = line.substringAfter(":").split(",")
                        .map { it.trim() }
                        .filter { it.isNotBlank() }
                    results["topics"] = topics
                }
                line.startsWith("Confidence:", ignoreCase = true) -> {
                    val confidence = line.substringAfter(":").trim().toDoubleOrNull() ?: 0.75
                    results["confidence"] = confidence
                }
                line.startsWith("Key Insights:", ignoreCase = true) -> {
                    results["insights"] = line.substringAfter(":").trim()
                }
            }
        }

        // Ensure required fields exist
        results.putIfAbsent("sentiment", "neutral")
        results.putIfAbsent("complexity", "medium")
        results.putIfAbsent("topics", listOf("general"))
        results.putIfAbsent("confidence", 0.75)

        return results
    }

    /**
     * Validate prompt is not blank.
     */
    private fun validatePrompt(prompt: String) {
        require(prompt.isNotBlank()) { "Prompt cannot be blank" }
    }

    /**
     * Handle generation errors with logging and security context.
     */
    private fun handleGenerationError(error: Exception) {
        when (error) {
            is IllegalArgumentException -> logger.w(TAG, "Invalid request: ${error.message}")
            is SecurityException -> {
                logger.e(TAG, "Security violation in AI request", error)
                securityContext.logSecurityEvent("GEMINI_SECURITY_ERROR", error.message)
            }
            else -> logger.e(TAG, "Gemini API error: ${error.javaClass.simpleName}", error)
        }
    }

    companion object {
        private const val TAG = "RealVertexAIClient"
    }
}
