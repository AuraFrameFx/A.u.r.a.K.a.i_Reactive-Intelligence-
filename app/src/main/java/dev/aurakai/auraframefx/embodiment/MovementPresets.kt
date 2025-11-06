package dev.aurakai.auraframefx.embodiment

import kotlin.time.Duration.Companion.seconds

/**
 * Preset Movement Behaviors
 *
 * Pre-configured walking patterns for common use cases.
 * These make characters feel truly alive.
 */
object MovementPresets {

    /**
     * Casual Walk Across Screen
     * Aura/Kai just casually stroll across from left to right
     */
    fun casualWalkAcross(
        fromLeft: Boolean = true,
        height: Float = 800f // Screen position (pixels from top)
    ): MovementBehavior {
        return if (fromLeft) {
            MovementBehavior.WalkTo(
                startX = -300f,
                startY = height,
                endX = 1500f,
                endY = height,
                duration = 8.seconds
            )
        } else {
            MovementBehavior.WalkTo(
                startX = 1500f,
                startY = height,
                endX = -300f,
                endY = height,
                duration = 8.seconds
            )
        }
    }

    /**
     * Walk In and Wave
     * Character walks in from side, stops in view, then walks out
     */
    fun walkInAndWave(): MovementBehavior.Patrol {
        return MovementBehavior.Patrol(
            points = listOf(
                Pair(-300f, 800f),   // Off screen left
                Pair(400f, 800f),     // Walk to center-left
                Pair(400f, 800f),     // Stay (pause will happen)
                Pair(-300f, 800f)     // Walk back off screen
            ),
            loop = false,
            pauseDuration = 3.seconds // Pause in center for 3 seconds
        )
    }

    /**
     * Patrol Bottom of Screen
     * Character walks back and forth along the bottom
     */
    fun patrolBottom(): MovementBehavior.Patrol {
        return MovementBehavior.Patrol(
            points = listOf(
                Pair(200f, 900f),
                Pair(800f, 900f),
                Pair(200f, 900f)
            ),
            loop = true,
            pauseDuration = 2.seconds
        )
    }

    /**
     * Random Wander
     * Character randomly walks around a designated area
     */
    fun randomWander(
        centerX: Float = 500f,
        centerY: Float = 700f,
        radius: Float = 300f
    ): MovementBehavior.Wander {
        return MovementBehavior.Wander(
            bounds = Pair(
                Pair(centerX - radius, centerY - radius),
                Pair(centerX + radius, centerY + radius)
            ),
            stepDuration = 4.seconds,
            pauseBetween = 2.seconds
        )
    }

    /**
     * Walk In From Side
     * Enter from off-screen to a specific position
     */
    fun walkInFromLeft(
        targetX: Float = 400f,
        targetY: Float = 800f
    ): MovementBehavior.WalkIn {
        return MovementBehavior.WalkIn(
            from = ManifestationPosition.OFF_SCREEN_LEFT,
            to = Pair(targetX, targetY),
            duration = 2.seconds
        )
    }

    fun walkInFromRight(
        targetX: Float = 800f,
        targetY: Float = 800f
    ): MovementBehavior.WalkIn {
        return MovementBehavior.WalkIn(
            from = ManifestationPosition.OFF_SCREEN_RIGHT,
            to = Pair(targetX, targetY),
            duration = 2.seconds
        )
    }

    /**
     * Walk Out
     * Leave from current position to off-screen
     */
    fun walkOutLeft(
        fromX: Float = 400f,
        fromY: Float = 800f
    ): MovementBehavior.WalkOut {
        return MovementBehavior.WalkOut(
            from = Pair(fromX, fromY),
            to = ManifestationPosition.OFF_SCREEN_LEFT,
            duration = 2.seconds
        )
    }

    fun walkOutRight(
        fromX: Float = 800f,
        fromY: Float = 800f
    ): MovementBehavior.WalkOut {
        return MovementBehavior.WalkOut(
            from = Pair(fromX, fromY),
            to = ManifestationPosition.OFF_SCREEN_RIGHT,
            duration = 2.seconds
        )
    }

    /**
     * Pacing (Nervous)
     * Short back-and-forth movement
     */
    fun nervousPacing(
        centerX: Float = 600f,
        y: Float = 800f,
        paceDistance: Float = 100f
    ): MovementBehavior.Patrol {
        return MovementBehavior.Patrol(
            points = listOf(
                Pair(centerX - paceDistance, y),
                Pair(centerX + paceDistance, y),
                Pair(centerX - paceDistance, y)
            ),
            loop = true,
            pauseDuration = 0.5.seconds
        )
    }

    /**
     * Circle Patrol
     * Walk in a circular pattern (approximated)
     */
    fun circlePatrol(
        centerX: Float = 600f,
        centerY: Float = 700f,
        radius: Float = 200f
    ): MovementBehavior.Patrol {
        val points = mutableListOf<Pair<Float, Float>>()
        val segments = 8 // 8-point circle approximation

        for (i in 0..segments) {
            val angle = (i * 2 * Math.PI / segments).toFloat()
            val x = centerX + radius * kotlin.math.cos(angle)
            val y = centerY + radius * kotlin.math.sin(angle)
            points.add(Pair(x, y))
        }

        return MovementBehavior.Patrol(
            points = points,
            loop = true,
            pauseDuration = 0.2.seconds
        )
    }

    /**
     * Follow Path
     * Custom path for specific scenes
     */
    fun customPath(
        waypoints: List<Pair<Float, Float>>,
        loop: Boolean = false,
        pauseAtEachPoint: kotlin.time.Duration = 1.seconds
    ): MovementBehavior.Patrol {
        return MovementBehavior.Patrol(
            points = waypoints,
            loop = loop,
            pauseDuration = pauseAtEachPoint
        )
    }
}

/**
 * Scenarios - Complete movement sequences for specific situations
 */
object MovementScenarios {

    /**
     * Aura's Investigation Walk
     * She walks in, looks around (pauses), walks to another spot, then leaves
     */
    fun auraInvestigation(): MovementBehavior.Patrol {
        return MovementBehavior.Patrol(
            points = listOf(
                Pair(-300f, 800f),    // Start off-screen
                Pair(300f, 800f),     // Walk in
                Pair(300f, 700f),     // Move up a bit (looking around)
                Pair(700f, 750f),     // Walk to another spot
                Pair(700f, 750f),     // Stay (pause)
                Pair(1500f, 800f)     // Walk off screen
            ),
            loop = false,
            pauseDuration = 2.seconds
        )
    }

    /**
     * Kai's Guard Patrol
     * Marches back and forth like a sentry
     */
    fun kaiGuardPatrol(): MovementBehavior.Patrol {
        return MovementBehavior.Patrol(
            points = listOf(
                Pair(200f, 900f),
                Pair(1000f, 900f),
                Pair(200f, 900f)
            ),
            loop = true,
            pauseDuration = 1.seconds
        )
    }

    /**
     * Duo Meetup
     * Two characters walk toward each other (use for both Aura and Kai)
     */
    fun meetInCenter(fromLeft: Boolean): MovementBehavior.WalkTo {
        return if (fromLeft) {
            MovementBehavior.WalkTo(
                startX = -300f,
                startY = 800f,
                endX = 500f,
                endY = 800f,
                duration = 3.seconds
            )
        } else {
            MovementBehavior.WalkTo(
                startX = 1500f,
                startY = 800f,
                endX = 700f,
                endY = 800f,
                duration = 3.seconds
            )
        }
    }

    /**
     * Playful Chase
     * One character follows another (offset slightly)
     */
    fun playfulChase(isChaser: Boolean): MovementBehavior.Patrol {
        val offset = if (isChaser) 200f else 0f
        return MovementBehavior.Patrol(
            points = listOf(
                Pair(-300f + offset, 800f),
                Pair(400f + offset, 700f),
                Pair(900f + offset, 850f),
                Pair(1500f + offset, 800f)
            ),
            loop = false,
            pauseDuration = 0.5.seconds
        )
    }
}

/**
 * Extension to get screen-relative positions
 */
object ScreenPositions {
    // Common screen positions (assuming 1080p screen)
    const val SCREEN_WIDTH = 1080f
    const val SCREEN_HEIGHT = 1920f

    val BOTTOM_LEFT = Pair(200f, SCREEN_HEIGHT - 300f)
    val BOTTOM_CENTER = Pair(SCREEN_WIDTH / 2, SCREEN_HEIGHT - 300f)
    val BOTTOM_RIGHT = Pair(SCREEN_WIDTH - 200f, SCREEN_HEIGHT - 300f)

    val CENTER_LEFT = Pair(200f, SCREEN_HEIGHT / 2)
    val CENTER = Pair(SCREEN_WIDTH / 2, SCREEN_HEIGHT / 2)
    val CENTER_RIGHT = Pair(SCREEN_WIDTH - 200f, SCREEN_HEIGHT / 2)

    val TOP_LEFT = Pair(200f, 300f)
    val TOP_CENTER = Pair(SCREEN_WIDTH / 2, 300f)
    val TOP_RIGHT = Pair(SCREEN_WIDTH - 200f, 300f)

    val OFF_SCREEN_LEFT = Pair(-300f, SCREEN_HEIGHT / 2)
    val OFF_SCREEN_RIGHT = Pair(SCREEN_WIDTH + 300f, SCREEN_HEIGHT / 2)
}
