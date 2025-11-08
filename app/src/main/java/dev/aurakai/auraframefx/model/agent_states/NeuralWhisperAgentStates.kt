package dev.aurakai.auraframefx.model.agent_states

/**
 * Data models for NeuralWhisper AI agent context, learning, and conversation tracking.
 *
 * These models support the NeuralWhisper agent's conversational AI capabilities,
 * context management, and adaptive learning systems.
 */

@Suppress("unused") // Reserved for NeuralWhisperAgent implementation
data class ActiveContext(
    // Renamed from ActiveContexts (singular)
    val contextId: String,
    val description: String? = null,
    val relatedData: Map<String, String> = emptyMap(),
    val priority: Int = 0,
    val createdAt: Long = System.currentTimeMillis(),
    val lastAccessedAt: Long = System.currentTimeMillis(),
    val tags: List<String> = emptyList(),
    val isActive: Boolean = true
)

@Suppress("unused") // Reserved for NeuralWhisperAgent implementation
// ContextChain could be a list of context snapshots or events
data class ContextChainEvent(
    val eventId: String,
    val timestamp: Long = System.currentTimeMillis(),
    val contextSnapshot: String? = null, // e.g., JSON representation of a context state
    val previousEventId: String? = null,
    val eventType: String = "context_update", // e.g., "context_update", "context_switch", "context_merge"
    val confidence: Float = 1.0f,
    val metadata: Map<String, String> = emptyMap()
)

@Suppress("unused") // Reserved for NeuralWhisperAgent implementation
data class LearningEvent(
    val eventId: String,
    val description: String,
    val outcome: String? = null, // e.g., "positive_reinforcement", "correction"
    val dataLearned: Map<String, String> = emptyMap(),
    val timestamp: Long = System.currentTimeMillis(),
    val source: String? = null, // Where the learning came from (user feedback, auto-correction, etc.)
    val confidence: Float = 0.8f,
    val applied: Boolean = false, // Whether the learning has been applied to the model
    val category: String = "general" // e.g., "general", "language", "user_preference", "error_correction"
)
