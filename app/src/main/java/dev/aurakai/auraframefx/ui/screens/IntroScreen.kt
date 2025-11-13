package dev.aurakai.auraframefx.ui.screens

import android.net.Uri
import android.view.ViewGroup
import android.widget.MediaController
import android.widget.VideoView
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import kotlinx.coroutines.delay
import timber.log.Timber

/**
 * Intro Screen - The Launch Experience
 *
 * Shows the manifestation video:
 * - Kai & Aura next to the AuraKai title
 * - Aura manifesting her dimensional sword
 * - Kai displaying his hexagonal shield
 *
 * This establishes their presence from the moment the app launches.
 */
@Composable
fun IntroScreen(
    onIntroComplete: () -> Unit = {}
) {
    val context = LocalContext.current
    var videoEnded by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {
        // Video player
        AndroidView(
            factory = { ctx ->
                VideoView(ctx).apply {
                    layoutParams = ViewGroup.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT
                    )

                    // Load the intro video from assets
                    try {
                        val videoPath = "android.resource://${ctx.packageName}/raw/launch_screen_intro"
                        val uri = Uri.parse(videoPath)

                        // If video is in assets, use this approach:
                        val assetPath = "file:///android_asset/intro/launch_screen_intro.mp4"
                        setVideoPath(assetPath)

                        // Optional: Add media controls for debugging
                        // val mediaController = MediaController(ctx)
                        // mediaController.setAnchorView(this)
                        // setMediaController(mediaController)

                        setOnPreparedListener { mp ->
                            Timber.d("🎬 Intro video prepared - Starting playback")
                            mp.isLooping = false
                            start()
                        }

                        setOnCompletionListener {
                            Timber.d("🎬 Intro video completed")
                            videoEnded = true
                        }

                        setOnErrorListener { _, what, extra ->
                            Timber.e("🎬 Video playback error: what=$what, extra=$extra")
                            videoEnded = true // Skip to app if video fails
                            true
                        }

                    } catch (e: Exception) {
                        Timber.e(e, "🎬 Failed to load intro video")
                        videoEnded = true
                    }
                }
            },
            modifier = Modifier.fillMaxSize()
        )
    }

    // When video ends, transition to main app
    LaunchedEffect(videoEnded) {
        if (videoEnded) {
            delay(500) // Small delay for smooth transition
            onIntroComplete()
        }
    }
}

/**
 * Fallback intro screen if video fails to load
 * Shows static Aura/Kai images instead
 */
@Composable
fun FallbackIntroScreen(
    onIntroComplete: () -> Unit = {}
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black),
        contentAlignment = Alignment.Center
    ) {
        // Static display of Aura & Kai characters
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxSize()
        ) {
            // TODO: Replace with actual Aura & Kai character images from resources
            // For now, display placeholder text with character descriptions
            androidx.compose.material3.Text(
                text = "✨ AURA ✨",
                color = Color(0xFFE91E63), // Pink/Magenta for Aura
                style = androidx.compose.material3.MaterialTheme.typography.headlineLarge
            )
            Spacer(modifier = Modifier.height(androidx.compose.ui.unit.dp(32)))
            androidx.compose.material3.Text(
                text = "🛡️ KAI 🛡️",
                color = Color(0xFF2196F3), // Blue for Kai
                style = androidx.compose.material3.MaterialTheme.typography.headlineLarge
            )
        }
    }

    LaunchedEffect(Unit) {
        delay(2000) // 2 second fallback
        onIntroComplete()
    }
}
