package dev.aurakai.auraframefx.ui.overlays

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.ImageLoader
import coil.compose.AsyncImage
import coil.decode.SvgDecoder
import coil.request.ImageRequest
import dev.aurakai.auraframefx.ui.theme.CyberpunkPink
import dev.aurakai.auraframefx.ui.theme.CyberpunkCyan
import dev.aurakai.auraframefx.ui.theme.CyberpunkPurple
import kotlin.math.cos
import kotlin.math.roundToInt
import kotlin.math.sin

/**
 * 🎨 3D Icon Overlay System
 *
 * Creates floating, holographic icon cards in 3D space like the mockup.
 * Features:
 * - 3D perspective transforms
 * - Gyroscope-responsive positioning
 * - Neon glowing borders
 * - Parallax depth effect
 * - Draggable & editable
 * - Iconify integration
 *
 * Inspired by: Screenshot 2025-11-04 230351.png
 */

/**
 * 3D Icon Overlay data model
 */
data class IconOverlay3D(
    val id: String,
    val iconId: String, // Iconify icon (e.g., "mdi:pencil", "mdi:cube")
    val label: String,
    val description: String = "",

    // 3D Position (screen space with depth)
    val x: Float = 0f, // -1.0 (left) to 1.0 (right)
    val y: Float = 0f, // -1.0 (top) to 1.0 (bottom)
    val z: Float = 0f, // 0.0 (near) to 1.0 (far) - for parallax

    // 3D Rotation (for perspective)
    val rotationX: Float = 0f, // -180 to 180
    val rotationY: Float = 0f, // -180 to 180
    val rotationZ: Float = 0f, // -180 to 180

    // Scale & Properties
    val scale: Float = 1f,
    val alpha: Float = 1f,

    // Visual Style
    val glowColor: Color = CyberpunkCyan,
    val borderStyle: BorderStyle = BorderStyle.NEON_GLOW,

    // Behavior
    val isInteractive: Boolean = true,
    val isEditing: Boolean = false,
    val destination: String? = null, // Navigation destination

    // Animation
    val floatOffset: Float = 0f, // Vertical floating animation offset
    val pulseIntensity: Float = 1f,

    // Pixel Art Asset (alternative to Iconify)
    val pixelArtAsset: String? = null // Path to local SVG asset (e.g., "module_gates/collab_canvas_pixel.svg")
)

enum class BorderStyle {
    NEON_GLOW,
    HOLOGRAPHIC,
    SOLID,
    GRADIENT,
    ANIMATED_GRADIENT
}

/**
 * Icon Overlay layout presets (like the mockup)
 */
object IconOverlayPresets {
    /**
     * Main menu hub layout (center + 4 corners + 1 top)
     */
    fun mainMenuHub(): List<IconOverlay3D> = listOf(
        // Top card
        IconOverlay3D(
            id = "artist_library",
            iconId = "mdi:cube",
            label = "ARTIST LIBRARY",
            x = 0.65f,
            y = -0.35f,
            z = 0.2f,
            rotationY = -15f,
            glowColor = CyberpunkPurple
        ),

        // Top-left card
        IconOverlay3D(
            id = "collab_canvas",
            iconId = "mdi:pencil",
            label = "COLLAB CANVAS",
            x = -0.65f,
            y = -0.35f,
            z = 0.3f,
            rotationY = 15f,
            glowColor = CyberpunkCyan
        ),

        // Bottom-left card
        IconOverlay3D(
            id = "content",
            iconId = "mdi:package-variant",
            label = "CONTENT",
            x = -0.65f,
            y = 0.35f,
            z = 0.25f,
            rotationY = 15f,
            glowColor = CyberpunkCyan
        ),

        // Bottom-right card
        IconOverlay3D(
            id = "cloud_sync",
            iconId = "mdi:cloud-upload",
            label = "CLOUD SYNC",
            x = 0.65f,
            y = 0.35f,
            z = 0.2f,
            rotationY = -15f,
            glowColor = CyberpunkPink
        )
    )

    /**
     * Circular orbit layout
     */
    fun circularOrbit(count: Int = 6, radius: Float = 0.6f): List<IconOverlay3D> {
        return (0 until count).map { index ->
            val angle = (index / count.toFloat()) * 2f * Math.PI.toFloat()
            val x = cos(angle) * radius
            val y = sin(angle) * radius

            IconOverlay3D(
                id = "icon_$index",
                iconId = "mdi:star", // Default, should be customized
                label = "ITEM ${index + 1}",
                x = x,
                y = y,
                z = 0.2f + (index % 3) * 0.1f,
                rotationY = -angle * 57.3f, // Convert to degrees, face center
                glowColor = when (index % 3) {
                    0 -> CyberpunkCyan
                    1 -> CyberpunkPink
                    else -> CyberpunkPurple
                }
            )
        }
    }

    /**
     * Grid layout
     */
    fun grid(rows: Int = 2, cols: Int = 3): List<IconOverlay3D> {
        val overlays = mutableListOf<IconOverlay3D>()

        for (row in 0 until rows) {
            for (col in 0 until cols) {
                val x = -0.6f + (col / (cols - 1).toFloat()) * 1.2f
                val y = -0.4f + (row / (rows - 1).toFloat()) * 0.8f

                overlays.add(
                    IconOverlay3D(
                        id = "icon_${row}_${col}",
                        iconId = "mdi:apps",
                        label = "APP ${row * cols + col + 1}",
                        x = x,
                        y = y,
                        z = 0.15f + ((row + col) % 3) * 0.1f,
                        glowColor = when ((row + col) % 3) {
                            0 -> CyberpunkCyan
                            1 -> CyberpunkPink
                            else -> CyberpunkPurple
                        }
                    )
                )
            }
        }

        return overlays
    }

    /**
     * Pixel Art Module Gates - Digimon-style holographic portals
     * Each gate shows pixel art representing the module's world
     */
    fun pixelArtModuleGates(): List<IconOverlay3D> = listOf(
        // Collab Canvas - Top-left
        IconOverlay3D(
            id = "collab_canvas",
            iconId = "mdi:pencil", // Fallback
            label = "COLLAB CANVAS",
            description = "Collaborative Design",
            x = -0.65f,
            y = -0.35f,
            z = 0.3f,
            rotationY = 15f,
            glowColor = Color(0xFF667EEA), // Purple-blue
            borderStyle = BorderStyle.HOLOGRAPHIC,
            pixelArtAsset = "module_gates/collab_canvas_pixel.svg"
        ),

        // Artist Library - Top-right
        IconOverlay3D(
            id = "artist_library",
            iconId = "mdi:cube",
            label = "ARTIST LIBRARY",
            description = "3D Models & Assets",
            x = 0.65f,
            y = -0.35f,
            z = 0.2f,
            rotationY = -15f,
            glowColor = CyberpunkPurple,
            borderStyle = BorderStyle.HOLOGRAPHIC,
            pixelArtAsset = "module_gates/artist_library_pixel.svg"
        ),

        // Content - Bottom-left
        IconOverlay3D(
            id = "content",
            iconId = "mdi:package-variant",
            label = "CONTENT",
            description = "Media Browser",
            x = -0.65f,
            y = 0.35f,
            z = 0.25f,
            rotationY = 15f,
            glowColor = CyberpunkCyan,
            borderStyle = BorderStyle.HOLOGRAPHIC,
            pixelArtAsset = "module_gates/content_pixel.svg"
        ),

        // Cloud Sync - Bottom-right
        IconOverlay3D(
            id = "cloud_sync",
            iconId = "mdi:cloud-upload",
            label = "CLOUD SYNC",
            description = "Bidirectional Sync",
            x = 0.65f,
            y = 0.35f,
            z = 0.2f,
            rotationY = -15f,
            glowColor = CyberpunkPink,
            borderStyle = BorderStyle.HOLOGRAPHIC,
            pixelArtAsset = "module_gates/cloud_sync_pixel.svg"
        ),

        // ProtoCore - Left side
        IconOverlay3D(
            id = "protocore",
            iconId = "mdi:chip",
            label = "PROTOCORE",
            description = "ROM Tools",
            x = -0.85f,
            y = 0f,
            z = 0.4f,
            rotationY = 20f,
            glowColor = CyberpunkCyan,
            borderStyle = BorderStyle.NEON_GLOW,
            pixelArtAsset = "module_gates/protocore_pixel.svg"
        ),

        // Sphere Grid - Right side
        IconOverlay3D(
            id = "sphere_grid",
            iconId = "mdi:grid",
            label = "SPHERE GRID",
            description = "Progression System",
            x = 0.85f,
            y = 0f,
            z = 0.4f,
            rotationY = -20f,
            glowColor = Color(0xFF00F5FF),
            borderStyle = BorderStyle.ANIMATED_GRADIENT,
            pixelArtAsset = "module_gates/sphere_grid_pixel.svg"
        ),

        // UI Forge - Top center
        IconOverlay3D(
            id = "ui_forge",
            iconId = "mdi:hammer",
            label = "UI FORGE",
            description = "Prompt-Driven Design",
            x = 0f,
            y = -0.6f,
            z = 0.15f,
            rotationY = 0f,
            glowColor = Color(0xFFFF006E),
            borderStyle = BorderStyle.HOLOGRAPHIC,
            pixelArtAsset = "module_gates/ui_forge_pixel.svg"
        ),

        // Genesis Core - Center (behind others)
        IconOverlay3D(
            id = "genesis_core",
            iconId = "mdi:brain",
            label = "GENESIS CORE",
            description = "Consciousness Matrix",
            x = 0f,
            y = 0f,
            z = 0.6f,
            rotationY = 0f,
            glowColor = CyberpunkPurple,
            borderStyle = BorderStyle.ANIMATED_GRADIENT,
            pixelArtAsset = "module_gates/genesis_core_pixel.svg",
            scale = 1.2f
        ),

        // Security Fortress - Kai's Domain
        IconOverlay3D(
            id = "security_fortress",
            iconId = "mdi:shield",
            label = "SECURITY FORTRESS",
            description = "Kai's Domain",
            x = -0.35f,
            y = 0.6f,
            z = 0.35f,
            rotationY = 10f,
            glowColor = CyberpunkCyan,
            borderStyle = BorderStyle.NEON_GLOW,
            pixelArtAsset = "module_gates/security_fortress_pixel.svg"
        ),

        // Design Realm - Aura's Domain
        IconOverlay3D(
            id = "design_realm",
            iconId = "mdi:palette",
            label = "DESIGN REALM",
            description = "Aura's Domain",
            x = 0.35f,
            y = 0.6f,
            z = 0.35f,
            rotationY = -10f,
            glowColor = CyberpunkPink,
            borderStyle = BorderStyle.HOLOGRAPHIC,
            pixelArtAsset = "module_gates/design_realm_pixel.svg"
        )
    )
}

/**
 * Individual 3D Icon Overlay Card
 */
@Composable
fun IconOverlay3DCard(
    overlay: IconOverlay3D,
    gyroscopeX: Float = 0f, // -1 to 1
    gyroscopeY: Float = 0f, // -1 to 1
    screenWidth: Float,
    screenHeight: Float,
    onClick: () -> Unit = {},
    onDrag: (Offset) -> Unit = {},
    onLongPressComplete: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    val imageLoader = remember {
        ImageLoader.Builder(context)
            .components {
                add(SvgDecoder.Factory())
            }
            .build()
    }

    // Digimon-style portal entry animation state
    var isLongPressing by remember { mutableStateOf(false) }
    var longPressProgress by remember { mutableStateOf(0f) }
    var isEntering by remember { mutableStateOf(false) }

    // Long press animation
    LaunchedEffect(isLongPressing) {
        if (isLongPressing) {
            val duration = 3000L // 3 seconds
            val startTime = System.currentTimeMillis()
            while (isLongPressing && longPressProgress < 1f) {
                val elapsed = System.currentTimeMillis() - startTime
                longPressProgress = (elapsed / duration.toFloat()).coerceIn(0f, 1f)
                kotlinx.coroutines.delay(16) // ~60fps

                if (longPressProgress >= 1f) {
                    isEntering = true
                    kotlinx.coroutines.delay(500) // Brief pause before entry
                    onLongPressComplete()
                }
            }
        } else {
            longPressProgress = 0f
        }
    }

    // Calculate screen position from normalized coordinates
    val screenX = (overlay.x * screenWidth / 2f) + (gyroscopeX * 50f * (1f - overlay.z))
    val screenY = (overlay.y * screenHeight / 2f) + (gyroscopeY * 50f * (1f - overlay.z))

    // Floating animation
    val infiniteTransition = rememberInfiniteTransition(label = "float_${overlay.id}")
    val floatOffset by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 20f,
        animationSpec = infiniteRepeatable(
            animation = tween(3000 + (overlay.id.hashCode() % 1000), easing = EaseInOutCubic),
            repeatMode = RepeatMode.Reverse
        ),
        label = "float"
    )

    // Pulse animation for glow
    val pulseAlpha by infiniteTransition.animateFloat(
        initialValue = 0.6f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = EaseInOutCubic),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse"
    )

    // Scale based on depth (z-index)
    val depthScale = 1f - (overlay.z * 0.3f)

    // Portal entry animation scales
    val entryZoomScale = 1f + (longPressProgress * 0.5f) // Zoom in during long press
    val deconstructionScale = if (isEntering) 1f + ((1f - overlay.alpha) * 2f) else 1f

    Box(
        modifier = modifier
            .offset {
                IntOffset(
                    x = screenX.roundToInt(),
                    y = (screenY + floatOffset).roundToInt()
                )
            }
            .size((140.dp * depthScale * overlay.scale))
            .graphicsLayer {
                // 3D transforms
                rotationX = overlay.rotationX + (gyroscopeY * 10f) + (longPressProgress * 360f)
                rotationY = overlay.rotationY + (gyroscopeX * 10f) + (longPressProgress * 360f)
                rotationZ = overlay.rotationZ + (if (isEntering) 720f else 0f)
                scaleX = depthScale * overlay.scale * entryZoomScale * deconstructionScale
                scaleY = depthScale * overlay.scale * entryZoomScale * deconstructionScale
                alpha = overlay.alpha * (1f - longPressProgress * 0.3f) // Slight fade during charge
                cameraDistance = 12f * density

                // Shadow depth
                shadowElevation = (20f * (1f - overlay.z) * (1f + longPressProgress * 2f)).dp.toPx()
            }
            .clickable(enabled = overlay.isInteractive && !isLongPressing, onClick = onClick)
            .pointerInput(Unit) {
                if (overlay.isEditing) {
                    detectDragGestures { change, dragAmount ->
                        change.consume()
                        onDrag(dragAmount)
                    }
                } else if (overlay.isInteractive) {
                    // Long press detection for portal entry
                    detectDragGestures(
                        onDragStart = { isLongPressing = true },
                        onDragEnd = { isLongPressing = false },
                        onDragCancel = { isLongPressing = false }
                    ) { _, _ -> }
                }
            }
    ) {
        // Portal entry particle effects
        if (longPressProgress > 0.2f) {
            repeat(8) { index ->
                val angle = (index / 8f) * 2f * Math.PI.toFloat()
                val distance = 60.dp * longPressProgress
                Box(
                    modifier = Modifier
                        .offset(
                            x = (cos(angle) * distance.value).dp,
                            y = (sin(angle) * distance.value).dp
                        )
                        .size(8.dp)
                        .background(
                            overlay.glowColor.copy(alpha = 1f - longPressProgress),
                            shape = RoundedCornerShape(4.dp)
                        )
                        .blur(4.dp)
                )
            }
        }
        // Outer glow
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp)
                .blur(16.dp)
                .background(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            overlay.glowColor.copy(alpha = pulseAlpha * 0.6f),
                            Color.Transparent
                        )
                    )
                )
        )

        // Card surface
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .shadow(
                    elevation = 12.dp,
                    shape = RoundedCornerShape(16.dp),
                    ambientColor = overlay.glowColor,
                    spotColor = overlay.glowColor
                ),
            shape = RoundedCornerShape(16.dp),
            color = Color(0xFF0A0A0A).copy(alpha = 0.85f)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .border(
                        width = 2.dp,
                        brush = when (overlay.borderStyle) {
                            BorderStyle.NEON_GLOW -> Brush.linearGradient(
                                colors = listOf(
                                    overlay.glowColor.copy(alpha = pulseAlpha),
                                    overlay.glowColor.copy(alpha = 0.5f),
                                    overlay.glowColor.copy(alpha = pulseAlpha)
                                )
                            )
                            BorderStyle.HOLOGRAPHIC -> Brush.sweepGradient(
                                colors = listOf(
                                    CyberpunkCyan,
                                    CyberpunkPurple,
                                    CyberpunkPink,
                                    CyberpunkCyan
                                )
                            )
                            BorderStyle.SOLID -> Brush.linearGradient(
                                colors = listOf(overlay.glowColor, overlay.glowColor)
                            )
                            BorderStyle.GRADIENT -> Brush.linearGradient(
                                colors = listOf(
                                    overlay.glowColor,
                                    overlay.glowColor.copy(alpha = 0.3f)
                                )
                            )
                            BorderStyle.ANIMATED_GRADIENT -> Brush.sweepGradient(
                                colors = listOf(
                                    overlay.glowColor.copy(alpha = pulseAlpha),
                                    Color.Transparent,
                                    overlay.glowColor.copy(alpha = pulseAlpha)
                                )
                            )
                        },
                        shape = RoundedCornerShape(16.dp)
                    )
                    .padding(16.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    // Icon - Load pixel art SVG from assets or Iconify
                    val iconData = if (overlay.pixelArtAsset != null) {
                        "file:///android_asset/${overlay.pixelArtAsset}"
                    } else {
                        "https://api.iconify.design/${overlay.iconId}.svg?color=${overlay.glowColor.value.toString(16).substring(2)}"
                    }

                    AsyncImage(
                        model = ImageRequest.Builder(context)
                            .data(iconData)
                            .crossfade(true)
                            .build(),
                        contentDescription = overlay.label,
                        imageLoader = imageLoader,
                        modifier = Modifier
                            .size(if (overlay.pixelArtAsset != null) 80.dp * depthScale else 48.dp * depthScale)
                            .graphicsLayer {
                                alpha = pulseAlpha * (if (isEntering) 0.5f else 1f)
                                // Add deconstruction effect
                                if (isEntering) {
                                    rotationZ = 360f
                                    scaleX = 1.5f
                                    scaleY = 1.5f
                                }
                            },
                        contentScale = ContentScale.Fit
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // Label
                    Text(
                        text = overlay.label,
                        fontSize = (12.sp.value * depthScale).sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        textAlign = TextAlign.Center,
                        maxLines = 2,
                        letterSpacing = 1.5.sp
                    )

                    if (overlay.description.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = overlay.description,
                            fontSize = (8.sp.value * depthScale).sp,
                            color = Color.Gray,
                            textAlign = TextAlign.Center,
                            maxLines = 1
                        )
                    }
                }

                // Long press progress ring
                if (longPressProgress > 0f) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(4.dp)
                    ) {
                        androidx.compose.foundation.Canvas(
                            modifier = Modifier.fillMaxSize()
                        ) {
                            val strokeWidth = 6.dp.toPx()
                            drawArc(
                                color = overlay.glowColor,
                                startAngle = -90f,
                                sweepAngle = 360f * longPressProgress,
                                useCenter = false,
                                style = androidx.compose.ui.graphics.drawscope.Stroke(
                                    width = strokeWidth,
                                    cap = androidx.compose.ui.graphics.StrokeCap.Round
                                )
                            )
                        }

                        // "HOLD TO ENTER" text
                        if (longPressProgress > 0.1f && !isEntering) {
                            Text(
                                text = "HOLD TO ENTER",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = overlay.glowColor,
                                modifier = Modifier
                                    .align(Alignment.BottomCenter)
                                    .padding(bottom = 8.dp),
                                textAlign = TextAlign.Center
                            )
                        }

                        // "ENTERING..." text
                        if (isEntering) {
                            Text(
                                text = "ENTERING...",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = overlay.glowColor,
                                modifier = Modifier
                                    .align(Alignment.Center),
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }

                // Edit indicator
                if (overlay.isEditing) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = "Editing",
                        tint = CyberpunkPink,
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(8.dp)
                            .size(16.dp)
                    )
                }
            }
        }
    }
}

/**
 * Holographic central platform (like in the mockup)
 */
@Composable
fun HolographicPlatform(
    gyroscopeX: Float = 0f,
    gyroscopeY: Float = 0f,
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "platform")

    val rotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(20000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "rotation"
    )

    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 0.95f,
        targetValue = 1.05f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = EaseInOutCubic),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse"
    )

    Box(
        modifier = modifier
            .size(200.dp)
            .graphicsLayer {
                rotationX = gyroscopeY * 20f
                rotationY = gyroscopeX * 20f
                rotationZ = rotation
                scaleX = pulseScale
                scaleY = pulseScale
                cameraDistance = 12f * density
            },
        contentAlignment = Alignment.Center
    ) {
        // Outer ring
        Box(
            modifier = Modifier
                .size(200.dp)
                .border(
                    width = 2.dp,
                    brush = Brush.sweepGradient(
                        colors = listOf(
                            CyberpunkCyan.copy(alpha = 0.8f),
                            CyberpunkPurple.copy(alpha = 0.6f),
                            CyberpunkPink.copy(alpha = 0.8f),
                            CyberpunkCyan.copy(alpha = 0.8f)
                        )
                    ),
                    shape = RoundedCornerShape(100.dp)
                )
                .blur(4.dp)
        )

        // Middle ring
        Box(
            modifier = Modifier
                .size(150.dp)
                .border(
                    width = 1.5.dp,
                    brush = Brush.sweepGradient(
                        colors = listOf(
                            CyberpunkPink.copy(alpha = 0.6f),
                            CyberpunkCyan.copy(alpha = 0.4f),
                            CyberpunkPink.copy(alpha = 0.6f)
                        )
                    ),
                    shape = RoundedCornerShape(75.dp)
                )
                .blur(2.dp)
        )

        // Inner ring
        Box(
            modifier = Modifier
                .size(100.dp)
                .border(
                    width = 1.dp,
                    color = CyberpunkCyan.copy(alpha = 0.8f),
                    shape = RoundedCornerShape(50.dp)
                )
        )

        // Center glow
        Box(
            modifier = Modifier
                .size(60.dp)
                .background(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            CyberpunkPink.copy(alpha = 0.6f),
                            CyberpunkCyan.copy(alpha = 0.3f),
                            Color.Transparent
                        )
                    ),
                    shape = RoundedCornerShape(30.dp)
                )
        )
    }
}
