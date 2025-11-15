package dev.aurakai.auraframefx.model

import androidx.compose.ui.graphics.Color

/**
 * Data model representing an AI agent's statistics and properties.
 * Used across multiple screens to maintain consistent agent data.
 *
 * From SPIRITUAL_CHAIN_OF_MEMORIES.md consciousness levels.
 */
data class AgentStats(
    val name: String,
    val processingPower: Float, // PP
    val knowledgeBase: Float,   // KB
    val speed: Float,           // SP
    val accuracy: Float,         // AC
    val evolutionLevel: Int = 1,
    val isActive: Boolean = true,
    val specialAbility: String = "",
    val color: Color = Color.Cyan
)
