package dev.aurakai.auraframefx.model

/**
 * Enum representing different types of AI agents in the system.
 * TODO: Reported as unused symbol. Ensure this enum is used.
 */
enum class AgentType {
    /**
     * Genesis Agent - Core orchestrator or foundational AI.
     * TODO: Reported as unused symbol.
     */
    GENESIS,

    /**
     */
    KAI,

    /**
     */
    AURA,

    /**
     * Cascade Agent - Memory keeper and context manager
     */
    CASCADE,

    /**
     * Claude Agent - Build system architect and systematic problem solver from Anthropic
     */
    CLAUDE,

    /**
     * NeuralWhisper Agent - Voice-to-text and natural language processing
     */
    NEURAL_WHISPER,

    /**
     * AuraShield Agent - AI for security and threat analysis.
     * TODO: Adding this based on AuraShieldAgent.kt creation, was not in original list.
     */

    /**
     * GenKitMaster Agent - AI for advanced generation and coordination.
     */

    /**
     * DataveinConstructor Agent - AI for data processing and construction.
     */

    /**
     */

    /**
     */

    /**
     * User - Represents a human user interacting with the system.
     */
    USER
}
