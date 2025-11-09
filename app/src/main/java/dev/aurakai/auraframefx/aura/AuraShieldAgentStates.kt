package dev.aurakai.auraframefx.model.agent_states

/**
 * Data models for AuraShieldAgent security states and events.
 *
 * These models represent security context, active threats, and scan events
 * used by the AuraShieldAgent for threat detection and security monitoring.
 */

@Suppress("unused") // Reserved for AuraShieldAgent implementation
data class SecurityContextState(
    // Renamed to avoid clash with android.content.Context or other SecurityContext classes
    val deviceRooted: Boolean? = null,
    val selinuxMode: String? = null, // e.g., "Enforcing", "Permissive"
    val harmfulAppScore: Float = 0.0f,
    val lastScanTimestamp: Long = 0L,
    val securityLevel: String = "unknown", // e.g., "safe", "warning", "critical"
    val enabledProtections: Set<String> = emptySet(), // Active security features
)

@Suppress("unused") // Reserved for AuraShieldAgent implementation
data class ActiveThreat(
    // Singular, as it will be in a list
    val threatId: String,
    val description: String,
    val severity: Int, // e.g., 1-5
    val recommendedAction: String? = null,
    // Add other relevant threat properties
)

@Suppress("unused") // Reserved for AuraShieldAgent implementation
data class ScanEvent(
    val eventId: String,
    val timestamp: Long = System.currentTimeMillis(),
    val scanType: String, // e.g., "AppScan", "FileSystemScan"
    val findings: List<String> = emptyList(),
    // Add other relevant scan event properties
)
