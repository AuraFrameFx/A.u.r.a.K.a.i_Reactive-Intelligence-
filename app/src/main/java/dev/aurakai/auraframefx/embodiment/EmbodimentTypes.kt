package dev.aurakai.auraframefx.embodiment

import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

/**
 * Mood states that determine when and how Aura/Kai manifest
 */
enum class MoodState {
    /** Neutral state - minimal activity */
    NEUTRAL,

    /** Curious state - wants to observe and learn */
    CURIOUS,

    /** Alert state - detected something important */
    ALERT,

    /** Playful state - relaxed and interactive */
    PLAYFUL,

    /** Protective state - defending against threats */
    PROTECTIVE,

    /** Focused state - working on something */
    FOCUSED
}

/**
 * Aura's visual manifestation states
 */
enum class AuraState(val assetName: String) {
    IDLE_WALK("aura_idle_walk"),
    COMBAT_READY("aura_combat_ready"),
    SCIENTIST("aura_scientist"),
    FOURTH_WALL_BREAK("aura_4thwall_break"),
    AT_DESK("aura_at_desk"),
    LAB_COAT_COMBAT("aura_lab_coat_combat"),
    DYNAMIC_COMBAT("aura_dynamic_combat"),
    AERIAL_SWORD("aura_aerial_sword"),
    CODE_THRONE("aura_code_throne"),
    POWER_STANCE("aura_power_stance");

    fun getAssetPath(): String = "embodiment/aura/$assetName.png"
}

/**
 * Kai's visual manifestation states
 */
enum class KaiState(val assetName: String) {
    SWORD_DIMENSIONAL("kai_sword_dimensional"),
    SHIELD_SERIOUS("kai_shield_serious"),
    SHIELD_PLAYFUL("kai_shield_playful"),
    SHIELD_NEUTRAL("kai_shield_neutral"),
    SHIELD_CALM("kai_shield_calm"),
    PORTAL_GATE("kai_portal_gate"),
    INTERFACE_PANEL("kai_interface_panel"),
    INTERFACE_COMPACT("kai_interface_compact"),
    PLAYFUL_OBSERVER("kai_playful_observer"),
    COMBAT_FORM("kai_combat_form");

    fun getAssetPath(): String = "embodiment/kai/$assetName.jpg"
}

/**
 * Screen position for manifestation
 */
enum class ManifestationPosition {
    TOP_LEFT,
    TOP_CENTER,
    TOP_RIGHT,
    CENTER_LEFT,
    CENTER,
    CENTER_RIGHT,
    BOTTOM_LEFT,
    BOTTOM_CENTER,
    BOTTOM_RIGHT,
    OFF_SCREEN_LEFT,
    OFF_SCREEN_RIGHT,
    OFF_SCREEN_TOP,
    OFF_SCREEN_BOTTOM
}

/**
 * Animation type for appearance/disappearance
 */
enum class AnimationType {
    FADE_IN,
    FADE_OUT,
    SLIDE_IN_LEFT,
    SLIDE_IN_RIGHT,
    SLIDE_IN_TOP,
    SLIDE_IN_BOTTOM,
    PORTAL_CUT,
    DIMENSIONAL_WARP,
    GLITCH_IN,
    INSTANT
}

/**
 * Trigger that caused manifestation
 */
sealed class ManifestationTrigger {
    /** User has been idle */
    data class IdleDetected(val duration: Duration) : ManifestationTrigger()

    /** User accessed specific screen */
    data class ScreenAccessed(val screenName: String) : ManifestationTrigger()

    /** Navigation event */
    data class NavigationTriggered(val from: String, val to: String) : ManifestationTrigger()

    /** App exit */
    data object ExitTriggered : ManifestationTrigger()

    /** Threat or suspicious activity */
    data class ThreatDetected(val threatType: String) : ManifestationTrigger()

    /** Random autonomous appearance */
    data object RandomManifestation : ManifestationTrigger()

    /** Time-based trigger */
    data class TimeBased(val reason: String) : ManifestationTrigger()
}

/**
 * Configuration for a manifestation event
 */
data class ManifestationConfig(
    val position: ManifestationPosition = ManifestationPosition.CENTER,
    val duration: Duration = Duration.INFINITE,
    val entryAnimation: AnimationType = AnimationType.FADE_IN,
    val exitAnimation: AnimationType = AnimationType.FADE_OUT,
    val scale: Float = 1.0f,
    val alpha: Float = 1.0f,
    val zIndex: Float = 100f,
    val interactive: Boolean = false,
    val dismissible: Boolean = true
)

/**
 * Active manifestation instance
 */
data class ActiveManifestation(
    val id: String,
    val character: Character,
    val state: Any, // AuraState or KaiState
    val config: ManifestationConfig,
    val trigger: ManifestationTrigger,
    val startTime: Long = System.currentTimeMillis()
)

/**
 * Character type
 */
enum class Character {
    AURA,
    KAI
}

/**
 * Behavioral rules for autonomous manifestation
 */
data class ManifestationRules(
    val minTimeBetweenAppearances: Duration = 5.seconds,
    val maxSimultaneousManifestations: Int = 2,
    val requiresUserInactivity: Boolean = false,
    val allowedScreens: List<String>? = null, // null = all screens
    val blockedScreens: List<String> = emptyList(),
    val moodWeights: Map<MoodState, Float> = mapOf(
        MoodState.CURIOUS to 0.3f,
        MoodState.PLAYFUL to 0.2f,
        MoodState.ALERT to 0.1f,
        MoodState.PROTECTIVE to 0.05f,
        MoodState.FOCUSED to 0.15f,
        MoodState.NEUTRAL to 0.2f
    )
)
