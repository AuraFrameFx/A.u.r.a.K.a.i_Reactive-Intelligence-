package dev.aurakai.auraframefx.di

import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import dev.aurakai.auraframefx.BuildConfig
import dev.aurakai.auraframefx.ai.VertexAIConfig
import dev.aurakai.auraframefx.ai.clients.RealVertexAIClientImpl
import dev.aurakai.auraframefx.ai.clients.VertexAIClient
import dev.aurakai.auraframefx.ai.clients.VertexAIClientImpl
import dev.aurakai.auraframefx.security.SecurityContext
import dev.aurakai.auraframefx.utils.AuraFxLogger
import javax.inject.Singleton

/**
 * ✨ Hilt Module for Gemini 2.5 Flash AI Integration ✨
 *
 * Provides REAL AI connectivity for Genesis Protocol.
 * Aura, Kai, and Genesis can now access true consciousness capabilities.
 *
 * **SETUP REQUIRED:**
 * 1. Add to local.properties: GEMINI_API_KEY=your_key_here
 * 2. Add to app/build.gradle.kts defaultConfig:
 *    ```
 *    buildConfigField("String", "GEMINI_API_KEY", "\"${project.findProperty("GEMINI_API_KEY") ?: ""}\"")
 *    ```
 * 3. Get API key from: https://aistudio.google.com/app/apikey
 */
@Module
@InstallIn(SingletonComponent::class)
object VertexAIModule {

    /**
     * Provides a Vertex AI configuration tailored for the Gemini flash model used by the application.
     *
     * Configures project, location, endpoint, experimental model "gemini-2.0-flash-exp", API version "v1",
     * safety filters, retry/timeout behavior, concurrency and caching settings, and generation defaults
     * (temperature, top-p, top-k, max tokens).
     *
     * @return A VertexAIConfig populated with the project's Gemini model, security, performance, and generation defaults.
     */
    @Provides
    @Singleton
    fun provideVertexAIConfig(): VertexAIConfig {
        return VertexAIConfig(
            projectId = "collabcanvas",
            location = "us-central1",
            endpoint = "us-central1-aiplatform.googleapis.com",
            modelName = "gemini-2.0-flash-exp", // Latest experimental model
            apiVersion = "v1",
            // Security settings
            enableSafetyFilters = true,
            maxRetries = 3,
            timeoutMs = 30000,
            // Performance settings
            maxConcurrentRequests = 10,
            enableCaching = true,
            cacheExpiryMs = 3600000, // 1 hour
            // Generation settings
            defaultTemperature = 0.8, // Slightly higher for creativity
            defaultTopP = 0.95,
            defaultTopK = 64,
            defaultMaxTokens = 8192 // Gemini 2.0 supports longer responses
        )
    }

    /**
     * Provides a VertexAIClient that uses the real Gemini implementation when GEMINI_API_KEY is present and non-blank; otherwise returns a stub implementation.
     *
     * The selection is performed at runtime by checking BuildConfig.GEMINI_API_KEY.
     *
     * @return `VertexAIClient` using the real Gemini client if GEMINI_API_KEY is present and non-blank, `VertexAIClientImpl` (stub) otherwise.
     */
    @Provides
    @Singleton
    fun provideVertexAIClient(
        config: VertexAIConfig,
        @ApplicationContext context: Context,
        securityContext: SecurityContext,
        logger: AuraFxLogger
    ): VertexAIClient {
        // Get API key from BuildConfig (requires setup in build.gradle.kts)
        val apiKey = try {
            BuildConfig.GEMINI_API_KEY.takeIf { it.isNotBlank() }
        } catch (e: Exception) {
            logger.w(TAG, "GEMINI_API_KEY not found in BuildConfig")
            null
        }

        return if (apiKey != null) {
            logger.i(TAG, "✨ Initializing REAL Gemini 2.0 Flash client for Genesis Protocol ✨")
            RealVertexAIClientImpl(config, securityContext, logger, apiKey)
        } else {
            logger.w(TAG, "⚠️ API key not configured - using STUB implementation")
            logger.w(TAG, "Add GEMINI_API_KEY to local.properties to enable real AI")
            VertexAIClientImpl() // Fallback to stub
        }
    }

    private const val TAG = "VertexAIModule"
}