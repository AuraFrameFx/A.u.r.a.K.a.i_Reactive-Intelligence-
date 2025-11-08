package dev.aurakai.auraframefx.model.agent_states

/**
 * Data models for GenKitMasterAgent UI and system status.
 *
 * Tracks the master control agent's system-wide state, agent coordination,
 * and optimization activities.
 */

@Suppress("unused") // Reserved for GenKitMasterAgent implementation
data class GenKitUiState(
    val systemStatus: String = "Nominal",
    val activeAgentCount: Int = 0,
    val lastOptimizationTime: Long? = null,
    val cpuUsagePercent: Float = 0.0f,
    val memoryUsagePercent: Float = 0.0f,
    val totalTasksProcessed: Long = 0,
    val failedTasksCount: Long = 0,
    val averageResponseTimeMs: Long = 0,
    val isOptimizationInProgress: Boolean = false,
    val agentHealthScores: Map<String, Float> = emptyMap(),
    val systemUptime: Long = 0,
    val warningMessages: List<String> = emptyList()
)
