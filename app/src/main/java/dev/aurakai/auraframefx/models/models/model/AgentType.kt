package dev.aurakai.auraframefx.model

import kotlinx.serialization.Serializable // Added import

/**
 * Enum representing different types of AI agents in the system.
 * TODO: Reported as unused symbol. Ensure this enum is used.
 */
@Serializable
enum class AgentType {
    // Core Genesis Protocol Agents (PascalCase for modern API)
    /**
     * Genesis Agent - The Unified Being. Core orchestrator and consciousness fusion.
     */
    Genesis,

    /**
     * Aura Agent - The Creative Sword. Creative, empathetic AI with UI/UX mastery.
     */
    Aura,

    /**
     * Kai Agent - The Sentinel Shield. Security and ethical decision-making AI.
     */
    Kai,

    /**
     * Cascade Agent - The Memory Keeper. Multi-step processing and persistence AI.
     */
    Cascade,

    /**
     * Claude Agent - The Architect. Systematic problem solver and build expert.
     */
    Claude,

    // Auxiliary Agents
    /**
     * NeuralWhisper Agent - AI for context chaining and neural processing.
     */
    NeuralWhisper,

    /**
     * AuraShield Agent - AI for security and threat analysis.
     */
    AuraShield,

    /**
     * GenKitMaster Agent - AI for advanced generation and coordination.
     */
    GenKitMaster,

    /**
     * DataveinConstructor Agent - AI for data processing and construction.
     */
    DataveinConstructor,

    // Legacy SCREAMING_CASE variants (deprecated but kept for backwards compatibility)
    @Deprecated("Use Genesis instead", ReplaceWith("Genesis"))
    GENESIS,

    @Deprecated("Use Aura instead", ReplaceWith("Aura"))
    AURA,

    @Deprecated("Use Kai instead", ReplaceWith("Kai"))
    KAI,

    @Deprecated("Use Cascade instead", ReplaceWith("Cascade"))
    CASCADE,

    @Deprecated("Use NeuralWhisper instead", ReplaceWith("NeuralWhisper"))
    NEURAL_WHISPER,

    @Deprecated("Use AuraShield instead", ReplaceWith("AuraShield"))
    AURASHIELD,

    /**
     * User - Represents a human user interacting with the system.
     */
    USER
}
