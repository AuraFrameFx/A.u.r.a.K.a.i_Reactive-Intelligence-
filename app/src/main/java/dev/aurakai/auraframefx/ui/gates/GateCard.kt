package dev.aurakai.auraframefx.ui.gates

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import kotlin.math.sin
import kotlin.math.cos

/**
 * Gate card with hologram border, pixel art interior, and double-tap to enter
 */
@Composable
fun GateCard(
    config: GateConfig,
    modifier: Modifier = Modifier,
    onDoubleTap: () -> Unit = {}
) {
    var lastTapTime by remember { mutableLongStateOf(0L) }
    var scale by remember { mutableFloatStateOf(1f) }

    // Pulsing glow animation
    val infiniteTransition = rememberInfiniteTransition(label = "gate_pulse")
    val pulseAlpha by infiniteTransition.animateFloat(
        initialValue = 0.4f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse_alpha"
    )

    // Rotation animation for hologram effect
    val rotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(20000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "rotation"
    )

    // Double-tap handler
    LaunchedEffect(scale) {
        if (scale != 1f) {
            delay(150)
            scale = 1f
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(config.backgroundColor)
            .pointerInput(Unit) {
                detectTapGestures(
                    onTap = {
                        val currentTime = System.currentTimeMillis()
                        if (currentTime - lastTapTime < 300) {
                            // Double tap detected
                            scale = 0.95f
                            onDoubleTap()
                        }
                        lastTapTime = currentTime
                    }
                )
            },
        contentAlignment = Alignment.Center
    ) {
        // Animated background particles
        GateBackgroundParticles(config.borderColor, rotation)

        // Main gate container
        Column(
            modifier = Modifier
                .fillMaxWidth(0.85f)
                .fillMaxHeight(0.8f),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Gate Title with unique styling
            GateTitle(
                config = config,
                pulseAlpha = pulseAlpha
            )

            // Hologram bordered gate portal
            Box(
                modifier = Modifier
                    .weight(1f)
                    .aspectRatio(0.65f),
                contentAlignment = Alignment.Center
            ) {
                // Outer glow effect
                HologramGlow(
                    color = config.glowColor,
                    secondaryColor = config.secondaryGlowColor,
                    alpha = pulseAlpha
                )

                // Pixelated grid border
                HologramBorder(
                    config = config,
                    rotation = rotation,
                    pulseAlpha = pulseAlpha
                )

                // Inner content area - pixel art scene
                Box(
                    modifier = Modifier
                        .fillMaxSize(0.85f)
                        .background(Color.Black.copy(alpha = 0.7f)),
                    contentAlignment = Alignment.Center
                ) {
                    // Placeholder for pixel art
                    // In production, load actual pixel art images here
                    GatePlaceholderArt(config)
                }
            }

            // Gate description
            Text(
                text = config.description,
                style = config.titleStyle.textStyle.copy(fontSize = 12.sp),
                color = config.borderColor.copy(alpha = 0.8f),
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp)
            )

            // Double-tap hint
            Text(
                text = "⚡ DOUBLE TAP TO ENTER ⚡",
                style = config.titleStyle.textStyle.copy(fontSize = 10.sp),
                color = config.borderColor.copy(alpha = pulseAlpha * 0.6f),
                textAlign = TextAlign.Center
            )
        }
    }
}

/**
 * Animated background particles for depth
 */
@Composable
private fun GateBackgroundParticles(
    color: Color,
    rotation: Float
) {
    Canvas(modifier = Modifier.fillMaxSize()) {
        val width = size.width
        val height = size.height

        // Draw floating particles
        for (i in 0..20) {
            val x = (width * (i % 5) / 5f) + sin(rotation * 0.01f + i) * 20
            val y = (height * (i / 5) / 4f) + cos(rotation * 0.01f + i) * 20
            val particleAlpha = (sin(rotation * 0.02f + i) + 1f) * 0.15f

            drawCircle(
                color = color.copy(alpha = particleAlpha),
                radius = 2f,
                center = Offset(x, y)
            )
        }
    }
}

/**
 * Gate title with glitch and pixel effects
 */
@Composable
private fun GateTitle(
    config: GateConfig,
    pulseAlpha: Float
) {
    Box(contentAlignment = Alignment.Center) {
        // Glitch shadow layers
        if (config.titleStyle.glitchEffect) {
            Text(
                text = config.title,
                style = config.titleStyle.textStyle,
                color = config.titleStyle.secondaryColor?.copy(alpha = pulseAlpha * 0.4f)
                    ?: config.titleStyle.primaryColor.copy(alpha = 0.4f),
                modifier = Modifier.offset(x = 2.dp, y = 2.dp)
            )
            Text(
                text = config.title,
                style = config.titleStyle.textStyle,
                color = config.titleStyle.strokeColor?.copy(alpha = pulseAlpha * 0.3f)
                    ?: config.titleStyle.primaryColor.copy(alpha = 0.3f),
                modifier = Modifier.offset(x = (-2).dp, y = (-1).dp)
            )
        }

        // Main title
        Text(
            text = config.title,
            style = config.titleStyle.textStyle,
            color = config.titleStyle.primaryColor.copy(alpha = pulseAlpha)
        )
    }
}

/**
 * Hologram glow effect
 */
@Composable
private fun BoxScope.HologramGlow(
    color: Color,
    secondaryColor: Color?,
    alpha: Float
) {
    Canvas(modifier = Modifier.fillMaxSize()) {
        val glowRadius = size.minDimension * 0.55f

        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(
                    color.copy(alpha = alpha * 0.3f),
                    secondaryColor?.copy(alpha = alpha * 0.2f) ?: color.copy(alpha = alpha * 0.2f),
                    Color.Transparent
                )
            ),
            radius = glowRadius,
            center = center
        )
    }
}

/**
 * Pixelated hologram border with grid effect
 */
@Composable
private fun BoxScope.HologramBorder(
    config: GateConfig,
    rotation: Float,
    pulseAlpha: Float
) {
    Canvas(modifier = Modifier.fillMaxSize()) {
        val borderWidth = 12f
        val gridSize = 20f
        val cornerSize = 40f

        // Draw pixelated grid border
        for (x in 0..size.width.toInt() step gridSize.toInt()) {
            for (y in 0..size.height.toInt() step gridSize.toInt()) {
                if (x < borderWidth || x > size.width - borderWidth ||
                    y < borderWidth || y > size.height - borderWidth) {

                    val cellAlpha = ((sin(rotation * 0.01f + x * 0.01f + y * 0.01f) + 1f) * 0.25f + 0.5f) * pulseAlpha

                    drawRect(
                        color = config.borderColor.copy(alpha = cellAlpha),
                        topLeft = Offset(x.toFloat(), y.toFloat()),
                        size = Size(gridSize, gridSize)
                    )
                }
            }
        }

        // Corner accents
        val corners = listOf(
            Offset(0f, 0f),
            Offset(size.width - cornerSize, 0f),
            Offset(0f, size.height - cornerSize),
            Offset(size.width - cornerSize, size.height - cornerSize)
        )

        corners.forEach { corner ->
            rotate(degrees = rotation * 0.5f, pivot = corner + Offset(cornerSize / 2, cornerSize / 2)) {
                drawRect(
                    color = config.secondaryGlowColor ?: config.borderColor.copy(alpha = pulseAlpha * 0.8f),
                    topLeft = corner,
                    size = Size(cornerSize, cornerSize),
                    style = Stroke(width = 3f)
                )
            }
        }
    }
}

/**
 * Placeholder pixel art (will be replaced with actual images)
 */
@Composable
private fun GatePlaceholderArt(config: GateConfig) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "🎨\n${config.moduleId}\nPixel Art\nComing Soon",
            style = config.titleStyle.textStyle.copy(fontSize = 16.sp),
            color = config.borderColor.copy(alpha = 0.5f),
            textAlign = TextAlign.Center
        )
    }
}
