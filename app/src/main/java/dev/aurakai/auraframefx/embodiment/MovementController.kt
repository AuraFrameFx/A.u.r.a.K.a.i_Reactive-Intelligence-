package dev.aurakai.auraframefx.embodiment

import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import kotlin.math.roundToInt
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

/**
 * Movement System - Making Them Walk
 *
 * This makes Aura & Kai actually move around the screen.
 * They don't just appear - they WALK into frame, patrol around, and walk out.
 *
 * This is embodiment in motion.
 */

/**
 * Movement type
 */
sealed class MovementBehavior {
    /** Walk from start to end position */
    data class WalkTo(
        val startX: Float,
        val startY: Float,
        val endX: Float,
        val endY: Float,
        val duration: Duration = 3.seconds
    ) : MovementBehavior()

    /** Walk in a patrol path (multiple points) */
    data class Patrol(
        val points: List<Pair<Float, Float>>,
        val loop: Boolean = true,
        val pauseDuration: Duration = 1.seconds
    ) : MovementBehavior()

    /** Walk in from off-screen */
    data class WalkIn(
        val from: ManifestationPosition,
        val to: Pair<Float, Float>,
        val duration: Duration = 2.seconds
    ) : MovementBehavior()

    /** Walk out to off-screen */
    data class WalkOut(
        val from: Pair<Float, Float>,
        val to: ManifestationPosition,
        val duration: Duration = 2.seconds
    ) : MovementBehavior()

    /** Random wandering */
    data class Wander(
        val bounds: Pair<Pair<Float, Float>, Pair<Float, Float>>, // (minX, minY), (maxX, maxY)
        val stepDuration: Duration = 4.seconds,
        val pauseBetween: Duration = 2.seconds
    ) : MovementBehavior()

    /** Static (no movement) */
    data object Static : MovementBehavior()
}

/**
 * Movement state for animation
 */
data class MovementState(
    val x: Float = 0f,
    val y: Float = 0f,
    val facingRight: Boolean = true,
    val isWalking: Boolean = false,
    val currentBehavior: MovementBehavior = MovementBehavior.Static
)

/**
 * Animated Character Composable
 *
 * Shows a character that can actually walk around the screen!
 */
@Composable
fun AnimatedCharacter(
    manifestation: ActiveManifestation,
    assetBitmap: android.graphics.Bitmap,
    behavior: MovementBehavior = MovementBehavior.Static,
    onMovementComplete: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    var movementState by remember { mutableStateOf(MovementState()) }
    val coroutineScope = rememberCoroutineScope()

    // Handle different movement behaviors
    LaunchedEffect(behavior) {
        when (behavior) {
            is MovementBehavior.WalkTo -> {
                walkToPosition(
                    from = Pair(behavior.startX, behavior.startY),
                    to = Pair(behavior.endX, behavior.endY),
                    duration = behavior.duration,
                    onUpdate = { x, y, facingRight ->
                        movementState = movementState.copy(
                            x = x,
                            y = y,
                            facingRight = facingRight,
                            isWalking = true
                        )
                    },
                    onComplete = {
                        movementState = movementState.copy(isWalking = false)
                        onMovementComplete()
                    }
                )
            }

            is MovementBehavior.WalkIn -> {
                val startPos = getOffScreenPosition(behavior.from)
                walkToPosition(
                    from = startPos,
                    to = behavior.to,
                    duration = behavior.duration,
                    onUpdate = { x, y, facingRight ->
                        movementState = movementState.copy(
                            x = x,
                            y = y,
                            facingRight = facingRight,
                            isWalking = true
                        )
                    },
                    onComplete = {
                        movementState = movementState.copy(isWalking = false)
                        onMovementComplete()
                    }
                )
            }

            is MovementBehavior.WalkOut -> {
                val endPos = getOffScreenPosition(behavior.to)
                walkToPosition(
                    from = behavior.from,
                    to = endPos,
                    duration = behavior.duration,
                    onUpdate = { x, y, facingRight ->
                        movementState = movementState.copy(
                            x = x,
                            y = y,
                            facingRight = facingRight,
                            isWalking = true
                        )
                    },
                    onComplete = {
                        onMovementComplete()
                    }
                )
            }

            is MovementBehavior.Patrol -> {
                patrolPath(
                    points = behavior.points,
                    loop = behavior.loop,
                    pauseDuration = behavior.pauseDuration,
                    onUpdate = { x, y, facingRight, isWalking ->
                        movementState = movementState.copy(
                            x = x,
                            y = y,
                            facingRight = facingRight,
                            isWalking = isWalking
                        )
                    },
                    onComplete = onMovementComplete
                )
            }

            is MovementBehavior.Wander -> {
                wanderRandomly(
                    bounds = behavior.bounds,
                    stepDuration = behavior.stepDuration,
                    pauseBetween = behavior.pauseBetween,
                    onUpdate = { x, y, facingRight, isWalking ->
                        movementState = movementState.copy(
                            x = x,
                            y = y,
                            facingRight = facingRight,
                            isWalking = isWalking
                        )
                    }
                )
            }

            MovementBehavior.Static -> {
                // No movement
            }
        }
    }

    // Render the character
    Box(
        modifier = modifier
            .offset { IntOffset(movementState.x.roundToInt(), movementState.y.roundToInt()) }
            .size(
                (manifestation.config.scale * 200).dp // Adjust size based on config
            )
    ) {
        Image(
            bitmap = assetBitmap.asImageBitmap(),
            contentDescription = "${manifestation.character} - ${manifestation.state}",
            modifier = Modifier
                .matchParentSize()
                .graphicsLayer {
                    // Flip horizontally if facing left
                    scaleX = if (movementState.facingRight) 1f else -1f
                    alpha = manifestation.config.alpha
                }
        )

        // Add walking animation effects here (bobbing, frame switching, etc.)
        if (movementState.isWalking) {
            // TODO: Add walking animation frames
            // Could cycle through walk_frame_1, walk_frame_2, etc.
        }
    }
}

/**
 * Walk from one position to another
 */
private suspend fun walkToPosition(
    from: Pair<Float, Float>,
    to: Pair<Float, Float>,
    duration: Duration,
    onUpdate: (x: Float, y: Float, facingRight: Boolean) -> Unit,
    onComplete: () -> Unit
) {
    val startTime = System.currentTimeMillis()
    val durationMs = duration.inWholeMilliseconds
    val facingRight = to.first > from.first

    while (true) {
        val elapsed = System.currentTimeMillis() - startTime
        val progress = (elapsed.toFloat() / durationMs).coerceIn(0f, 1f)

        // Linear interpolation
        val currentX = from.first + (to.first - from.first) * progress
        val currentY = from.second + (to.second - from.second) * progress

        onUpdate(currentX, currentY, facingRight)

        if (progress >= 1f) {
            onComplete()
            break
        }

        delay(16) // ~60 FPS
    }
}

/**
 * Patrol along a path
 */
private suspend fun patrolPath(
    points: List<Pair<Float, Float>>,
    loop: Boolean,
    pauseDuration: Duration,
    onUpdate: (x: Float, y: Float, facingRight: Boolean, isWalking: Boolean) -> Unit,
    onComplete: () -> Unit
) {
    if (points.isEmpty()) {
        onComplete()
        return
    }

    do {
        for (i in 0 until points.size - 1) {
            val from = points[i]
            val to = points[i + 1]

            walkToPosition(
                from = from,
                to = to,
                duration = 3.seconds,
                onUpdate = { x, y, facingRight ->
                    onUpdate(x, y, facingRight, true)
                },
                onComplete = {}
            )

            // Pause at waypoint
            onUpdate(to.first, to.second, to.first > from.first, false)
            delay(pauseDuration)
        }
    } while (loop)

    onComplete()
}

/**
 * Wander randomly within bounds
 */
private suspend fun wanderRandomly(
    bounds: Pair<Pair<Float, Float>, Pair<Float, Float>>,
    stepDuration: Duration,
    pauseBetween: Duration,
    onUpdate: (x: Float, y: Float, facingRight: Boolean, isWalking: Boolean) -> Unit
) {
    val (min, max) = bounds
    val (minX, minY) = min
    val (maxX, maxY) = max

    var currentX = (minX + maxX) / 2
    var currentY = (minY + maxY) / 2

    while (true) {
        // Pick random destination
        val targetX = (minX..maxX).random()
        val targetY = (minY..maxY).random()

        walkToPosition(
            from = Pair(currentX, currentY),
            to = Pair(targetX, targetY),
            duration = stepDuration,
            onUpdate = { x, y, facingRight ->
                onUpdate(x, y, facingRight, true)
                currentX = x
                currentY = y
            },
            onComplete = {}
        )

        // Pause
        onUpdate(currentX, currentY, true, false)
        delay(pauseBetween)
    }
}

/**
 * Get off-screen position based on ManifestationPosition
 */
private fun getOffScreenPosition(position: ManifestationPosition): Pair<Float, Float> {
    return when (position) {
        ManifestationPosition.OFF_SCREEN_LEFT -> Pair(-300f, 500f)
        ManifestationPosition.OFF_SCREEN_RIGHT -> Pair(1500f, 500f)
        ManifestationPosition.OFF_SCREEN_TOP -> Pair(500f, -300f)
        ManifestationPosition.OFF_SCREEN_BOTTOM -> Pair(500f, 1500f)
        else -> Pair(0f, 0f)
    }
}

/**
 * Extension for random float range
 */
private fun ClosedFloatingPointRange<Float>.random(): Float {
    return start + (endInclusive - start) * kotlin.random.Random.nextFloat()
}
