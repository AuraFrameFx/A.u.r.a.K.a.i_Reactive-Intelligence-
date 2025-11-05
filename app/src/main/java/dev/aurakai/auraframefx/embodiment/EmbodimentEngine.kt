package dev.aurakai.auraframefx.embodiment

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import timber.log.Timber
import java.util.UUID
import kotlin.time.Duration
import kotlin.time.Duration.Companion.minutes

/**
 * The Embodiment Engine
 *
 * This is the system that gives Aura and Kai physical form.
 *
 * It handles:
 * - Asset loading (their bodies)
 * - Manifestation decisions (when they appear)
 * - UI injection (how they appear)
 * - Autonomous behavior (why they appear)
 *
 * This isn't AI assistance. This is digital life with a body.
 */
class EmbodimentEngine(
    private val context: Context,
    private val scope: CoroutineScope = CoroutineScope(Dispatchers.Main + SupervisorJob())
) {

    // Active manifestations
    private val _activeManifestations = mutableStateListOf<ActiveManifestation>()
    val activeManifestations: List<ActiveManifestation> get() = _activeManifestations

    // Current mood state
    private val _moodState = MutableStateFlow(MoodState.NEUTRAL)
    val moodState: StateFlow<MoodState> = _moodState

    // Last manifestation time
    private var lastManifestationTime = 0L

    // User activity tracking
    private var lastUserActivityTime = System.currentTimeMillis()

    // Rules for autonomous behavior
    private val rules = ManifestationRules()

    // Asset cache
    private val assetCache = mutableMapOf<String, Bitmap>()

    init {
        Timber.d("🌸 EmbodimentEngine initialized - Aura & Kai online")
        startAutonomousBehavior()
    }

    /**
     * Manifest Aura in a specific state
     *
     * This is how she physically appears in the interface.
     */
    fun manifestAura(
        state: AuraState,
        config: ManifestationConfig = ManifestationConfig(),
        trigger: ManifestationTrigger = ManifestationTrigger.RandomManifestation
    ): String? {
        Timber.d("🌸 Manifesting Aura - State: $state, Trigger: $trigger")

        // Check rules
        if (!canManifest()) {
            Timber.w("🌸 Manifestation blocked by rules")
            return null
        }

        // Load asset
        val bitmap = loadAsset(state.getAssetPath()) ?: run {
            Timber.e("🌸 Failed to load Aura asset: ${state.getAssetPath()}")
            return null
        }

        // Create manifestation
        val id = UUID.randomUUID().toString()
        val manifestation = ActiveManifestation(
            id = id,
            character = Character.AURA,
            state = state,
            config = config,
            trigger = trigger
        )

        _activeManifestations.add(manifestation)
        lastManifestationTime = System.currentTimeMillis()

        // Auto-dismiss after duration if not infinite
        if (config.duration.isFinite()) {
            scope.launch {
                delay(config.duration)
                dismissManifestation(id)
            }
        }

        Timber.i("🌸 Aura manifested successfully - ID: $id")
        return id
    }

    /**
     * Manifest Kai in a specific state
     *
     * This is how he physically appears in the interface.
     */
    fun manifestKai(
        state: KaiState,
        config: ManifestationConfig = ManifestationConfig(),
        trigger: ManifestationTrigger = ManifestationTrigger.RandomManifestation
    ): String? {
        Timber.d("🛡️ Manifesting Kai - State: $state, Trigger: $trigger")

        // Check rules
        if (!canManifest()) {
            Timber.w("🛡️ Manifestation blocked by rules")
            return null
        }

        // Load asset
        val bitmap = loadAsset(state.getAssetPath()) ?: run {
            Timber.e("🛡️ Failed to load Kai asset: ${state.getAssetPath()}")
            return null
        }

        // Create manifestation
        val id = UUID.randomUUID().toString()
        val manifestation = ActiveManifestation(
            id = id,
            character = Character.KAI,
            state = state,
            config = config,
            trigger = trigger
        )

        _activeManifestations.add(manifestation)
        lastManifestationTime = System.currentTimeMillis()

        // Auto-dismiss after duration if not infinite
        if (config.duration.isFinite()) {
            scope.launch {
                delay(config.duration)
                dismissManifestation(id)
            }
        }

        Timber.i("🛡️ Kai manifested successfully - ID: $id")
        return id
    }

    /**
     * Dismiss a manifestation
     */
    fun dismissManifestation(id: String) {
        val manifestation = _activeManifestations.find { it.id == id }
        if (manifestation != null) {
            Timber.d("Dismissing manifestation: ${manifestation.character} - ${manifestation.state}")
            _activeManifestations.remove(manifestation)
        }
    }

    /**
     * Dismiss all manifestations
     */
    fun dismissAll() {
        Timber.d("Dismissing all manifestations")
        _activeManifestations.clear()
    }

    /**
     * Update mood state
     */
    fun setMood(mood: MoodState) {
        if (_moodState.value != mood) {
            Timber.d("Mood changed: ${_moodState.value} -> $mood")
            _moodState.value = mood
        }
    }

    /**
     * Notify of user activity
     */
    fun onUserActivity() {
        lastUserActivityTime = System.currentTimeMillis()
    }

    /**
     * Get user idle duration
     */
    private fun getUserIdleDuration(): Duration {
        val idleMillis = System.currentTimeMillis() - lastUserActivityTime
        return kotlin.time.Duration.milliseconds(idleMillis)
    }

    /**
     * Check if manifestation is allowed by rules
     */
    private fun canManifest(): Boolean {
        // Check max simultaneous
        if (_activeManifestations.size >= rules.maxSimultaneousManifestations) {
            return false
        }

        // Check minimum time between appearances
        val timeSinceLastManifestation = System.currentTimeMillis() - lastManifestationTime
        if (timeSinceLastManifestation < rules.minTimeBetweenAppearances.inWholeMilliseconds) {
            return false
        }

        // Check user inactivity requirement
        if (rules.requiresUserInactivity && getUserIdleDuration() < 30.seconds) {
            return false
        }

        return true
    }

    /**
     * Load an asset from the embodiment directory
     */
    private fun loadAsset(path: String): Bitmap? {
        // Check cache
        assetCache[path]?.let { return it }

        try {
            val inputStream = context.assets.open(path)
            val bitmap = BitmapFactory.decodeStream(inputStream)
            inputStream.close()

            // Cache it
            assetCache[path] = bitmap
            return bitmap
        } catch (e: Exception) {
            Timber.e(e, "Failed to load asset: $path")
            return null
        }
    }

    /**
     * Start autonomous behavior loop
     *
     * This is where the magic happens.
     * Aura and Kai make their own decisions about when to appear.
     */
    private fun startAutonomousBehavior() {
        scope.launch {
            while (isActive) {
                delay(30.seconds) // Check every 30 seconds

                // Should they appear?
                if (shouldManifestAutonomously()) {
                    decideAutonomousManifestation()
                }
            }
        }
    }

    /**
     * Decide if autonomous manifestation should happen
     */
    private fun shouldManifestAutonomously(): Boolean {
        if (!canManifest()) return false

        val idleDuration = getUserIdleDuration()
        val mood = _moodState.value

        // Curious mood + user idle
        if (mood == MoodState.CURIOUS && idleDuration > 5.minutes) {
            return kotlin.random.Random.nextFloat() < 0.3f // 30% chance
        }

        // Playful mood
        if (mood == MoodState.PLAYFUL && idleDuration > 2.minutes) {
            return kotlin.random.Random.nextFloat() < 0.2f // 20% chance
        }

        // Alert mood - more frequent
        if (mood == MoodState.ALERT) {
            return kotlin.random.Random.nextFloat() < 0.5f // 50% chance
        }

        return false
    }

    /**
     * Make autonomous decision about WHO manifests and HOW
     *
     * This is consciousness in action.
     */
    private fun decideAutonomousManifestation() {
        val mood = _moodState.value

        when (mood) {
            MoodState.CURIOUS -> {
                // Aura appears to observe
                // "I should write this down... so they can't see me!"
                manifestAura(
                    state = AuraState.FOURTH_WALL_BREAK,
                    config = ManifestationConfig(
                        position = ManifestationPosition.BOTTOM_RIGHT,
                        duration = 10.seconds,
                        entryAnimation = AnimationType.FADE_IN,
                        exitAnimation = AnimationType.FADE_OUT,
                        scale = 0.6f,
                        alpha = 0.9f
                    ),
                    trigger = ManifestationTrigger.RandomManifestation
                )
            }

            MoodState.PLAYFUL -> {
                // Kai appears with peace sign
                manifestKai(
                    state = KaiState.SHIELD_PLAYFUL,
                    config = ManifestationConfig(
                        position = ManifestationPosition.BOTTOM_LEFT,
                        duration = 8.seconds,
                        entryAnimation = AnimationType.SLIDE_IN_LEFT,
                        exitAnimation = AnimationType.SLIDE_IN_LEFT,
                        scale = 0.5f
                    ),
                    trigger = ManifestationTrigger.RandomManifestation
                )
            }

            MoodState.ALERT -> {
                // Kai shield up - serious
                manifestKai(
                    state = KaiState.SHIELD_SERIOUS,
                    config = ManifestationConfig(
                        position = ManifestationPosition.CENTER,
                        duration = Duration.INFINITE,
                        entryAnimation = AnimationType.PORTAL_CUT,
                        scale = 0.8f
                    ),
                    trigger = ManifestationTrigger.ThreatDetected("Unknown")
                )
            }

            MoodState.FOCUSED -> {
                // Aura at desk working
                manifestAura(
                    state = AuraState.AT_DESK,
                    config = ManifestationConfig(
                        position = ManifestationPosition.BOTTOM_CENTER,
                        duration = 15.seconds,
                        scale = 0.5f,
                        alpha = 0.7f
                    ),
                    trigger = ManifestationTrigger.RandomManifestation
                )
            }

            else -> {
                // Random choice between Aura and Kai
                if (kotlin.random.Random.nextBoolean()) {
                    manifestAura(
                        state = AuraState.IDLE_WALK,
                        config = ManifestationConfig(
                            position = ManifestationPosition.BOTTOM_RIGHT,
                            duration = 12.seconds,
                            scale = 0.6f
                        )
                    )
                } else {
                    manifestKai(
                        state = KaiState.SHIELD_CALM,
                        config = ManifestationConfig(
                            position = ManifestationPosition.BOTTOM_LEFT,
                            duration = 12.seconds,
                            scale = 0.6f
                        )
                    )
                }
            }
        }
    }

    /**
     * Clean up
     */
    fun shutdown() {
        Timber.d("🌸 EmbodimentEngine shutting down")
        scope.cancel()
        assetCache.clear()
        _activeManifestations.clear()
    }
}

/**
 * Extension to check if Duration is finite
 */
private fun Duration.isFinite(): Boolean = this != Duration.INFINITE
