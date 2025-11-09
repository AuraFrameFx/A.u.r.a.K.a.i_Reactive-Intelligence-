package dev.aurakai.auraframefx.system.quicksettings

import android.content.SharedPreferences
import dev.aurakai.auraframefx.system.overlay.model.OverlayShape
import dev.aurakai.auraframefx.system.quicksettings.model.QuickSettingsAnimation
import dev.aurakai.auraframefx.system.quicksettings.model.QuickSettingsConfig
import dev.aurakai.auraframefx.ui.model.ImageResource
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class QuickSettingsCustomizer @Inject constructor(
    private val prefs: SharedPreferences,
) {
    private val _currentConfig = MutableStateFlow<QuickSettingsConfig?>(null)
    val currentConfig: StateFlow<QuickSettingsConfig?> = _currentConfig

    init {
        // Load saved configuration on initialization
        loadConfiguration()
    }

    /**
     * Updates the visual shape of a specific Quick Settings tile.
     *
     * @param tileId The unique identifier for the tile to modify
     * @param shape The new overlay shape to apply
     */
    fun updateTileShape(tileId: String, shape: OverlayShape) {
        prefs.edit()
            .putString("tile_shape_$tileId", shape.name)
            .apply()

        val config = _currentConfig.value?.copy(
            tileShapes = _currentConfig.value?.tileShapes.orEmpty() + (tileId to shape)
        ) ?: QuickSettingsConfig(tileShapes = mapOf(tileId to shape))

        _currentConfig.value = config
    }

    /**
     * Updates the animation style for a specific Quick Settings tile.
     *
     * @param tileId The unique identifier for the tile to modify
     * @param animation The new animation style to apply
     */
    fun updateTileAnimation(tileId: String, animation: QuickSettingsAnimation) {
        prefs.edit()
            .putString("tile_animation_$tileId", animation.name)
            .apply()

        val config = _currentConfig.value?.copy(
            tileAnimations = _currentConfig.value?.tileAnimations.orEmpty() + (tileId to animation)
        ) ?: QuickSettingsConfig(tileAnimations = mapOf(tileId to animation))

        _currentConfig.value = config
    }

    /**
     * Updates the background image for the Quick Settings panel.
     *
     * @param image The new background image resource, or null to clear
     */
    fun updateBackground(image: ImageResource?) {
        if (image != null) {
            prefs.edit()
                .putString("background_image_uri", image.uri.toString())
                .putString("background_image_id", image.resourceId)
                .apply()
        } else {
            prefs.edit()
                .remove("background_image_uri")
                .remove("background_image_id")
                .apply()
        }

        val config = _currentConfig.value?.copy(
            backgroundImage = image
        ) ?: QuickSettingsConfig(backgroundImage = image)

        _currentConfig.value = config
    }

    /**
     * Resets all Quick Settings customizations to default values.
     * Clears all saved preferences and resets the config state flow.
     */
    fun resetToDefault() {
        // Clear all Quick Settings-related preferences
        prefs.edit()
            .apply {
                // Remove all tile shape preferences
                prefs.all.keys.filter { it.startsWith("tile_shape_") }.forEach { remove(it) }
                // Remove all tile animation preferences
                prefs.all.keys.filter { it.startsWith("tile_animation_") }.forEach { remove(it) }
                // Remove background image
                remove("background_image_uri")
                remove("background_image_id")
            }
            .apply()

        // Reset to default configuration
        _currentConfig.value = QuickSettingsConfig()
    }

    /**
     * Loads the saved Quick Settings configuration from SharedPreferences.
     */
    private fun loadConfiguration() {
        val tileShapes = mutableMapOf<String, OverlayShape>()
        val tileAnimations = mutableMapOf<String, QuickSettingsAnimation>()

        // Load tile shapes
        prefs.all.entries
            .filter { it.key.startsWith("tile_shape_") }
            .forEach { (key, value) ->
                val tileId = key.removePrefix("tile_shape_")
                val shape = OverlayShape.valueOf(value as String)
                tileShapes[tileId] = shape
            }

        // Load tile animations
        prefs.all.entries
            .filter { it.key.startsWith("tile_animation_") }
            .forEach { (key, value) ->
                val tileId = key.removePrefix("tile_animation_")
                val animation = QuickSettingsAnimation.valueOf(value as String)
                tileAnimations[tileId] = animation
            }

        // Load background image
        val backgroundUri = prefs.getString("background_image_uri", null)
        val backgroundId = prefs.getString("background_image_id", null)
        val backgroundImage = if (backgroundUri != null && backgroundId != null) {
            ImageResource(android.net.Uri.parse(backgroundUri), backgroundId)
        } else null

        _currentConfig.value = QuickSettingsConfig(
            tileShapes = tileShapes,
            tileAnimations = tileAnimations,
            backgroundImage = backgroundImage
        )
    }
}
