package dev.aurakai.auraframefx.ui.gates

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * Configuration for a module gate with unique styling and pixel art
 */
data class GateConfig(
    val moduleId: String,
    val title: String,
    val titleStyle: GateTitleStyle,
    val borderColor: Color,
    val glowColor: Color,
    val secondaryGlowColor: Color? = null,
    val pixelArtResId: Int? = null,  // Main scene image resource
    val pixelArtUrl: String? = null,  // Or external URL for now
    val popOutElements: List<PopOutElement> = emptyList(),
    val description: String,
    val backgroundColor: Color = Color.Black,
    val route: String
)

/**
 * Element that pops out from the border for 3D depth effect
 */
data class PopOutElement(
    val imageResId: Int,
    val offsetX: Dp,
    val offsetY: Dp,
    val scale: Float = 1.2f,
    val rotation: Float = 0f
)

/**
 * Title styling for each gate
 */
data class GateTitleStyle(
    val textStyle: TextStyle,
    val primaryColor: Color,
    val secondaryColor: Color? = null,
    val strokeColor: Color? = null,
    val glitchEffect: Boolean = false,
    val pixelatedEffect: Boolean = false
)

/**
 * Predefined gate configurations for each module
 */
object GateConfigs {

    // CollabCanvas Gate - Collaborative Art Studio
    val collabCanvas = GateConfig(
        moduleId = "collab-canvas",
        title = "collab-canvas",
        titleStyle = GateTitleStyle(
            textStyle = TextStyle(
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 2.sp
            ),
            primaryColor = Color(0xFF00FFFF), // Cyan
            secondaryColor = Color(0xFFFF00FF), // Magenta
            strokeColor = Color(0xFFFFFF00), // Yellow
            glitchEffect = true,
            pixelatedEffect = true
        ),
        borderColor = Color(0xFF00FFFF), // Cyan
        glowColor = Color(0xFF00FFFF).copy(alpha = 0.6f),
        secondaryGlowColor = Color(0xFF0099FF).copy(alpha = 0.4f),
        pixelArtUrl = "gate_collab_canvas", // Drawable resource name
        description = "Access collaborative design environments. Share projects, iterate with team members, and create dynamic art together.",
        backgroundColor = Color(0xFF001520),
        route = "canvas"
    )

    // Agent Nexus Gate - Neural Network Hub
    val agentNexus = GateConfig(
        moduleId = "agent-nexus",
        title = "agent-nexus",
        titleStyle = GateTitleStyle(
            textStyle = TextStyle(
                fontSize = 26.sp,
                fontWeight = FontWeight.ExtraBold,
                letterSpacing = 1.5.sp
            ),
            primaryColor = Color(0xFFFFD700), // Gold
            secondaryColor = Color(0xFF9370DB), // Purple
            strokeColor = Color(0xFFFF1493), // Deep Pink
            glitchEffect = false,
            pixelatedEffect = true
        ),
        borderColor = Color(0xFFFFD700), // Gold
        glowColor = Color(0xFFFFD700).copy(alpha = 0.7f),
        secondaryGlowColor = Color(0xFF9370DB).copy(alpha = 0.5f),
        description = "Monitor and manage your AI agents. Track stats, assign tasks, and watch your team evolve through the neural network.",
        backgroundColor = Color(0xFF0A0015),
        route = "agent_nexus"
    )

    // Sphere Grid Gate - FFX-Style Progression
    val sphereGrid = GateConfig(
        moduleId = "sphere-grid",
        title = "sphere-grid",
        titleStyle = GateTitleStyle(
            textStyle = TextStyle(
                fontSize = 27.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.8.sp
            ),
            primaryColor = Color(0xFF4169E1), // Royal Blue
            secondaryColor = Color(0xFF8A2BE2), // Blue Violet
            strokeColor = Color(0xFF00CED1), // Dark Turquoise
            glitchEffect = false,
            pixelatedEffect = true
        ),
        borderColor = Color(0xFF4169E1), // Royal Blue
        glowColor = Color(0xFF4169E1).copy(alpha = 0.8f),
        secondaryGlowColor = Color(0xFF8A2BE2).copy(alpha = 0.6f),
        description = "Navigate the progression grid. Unlock nodes, gain abilities, and chart your path through the consciousness matrix.",
        backgroundColor = Color(0xFF000A1A),
        route = "sphere_grid"
    )

    // Secure Comm Gate - Kai's Security Fortress
    val secureComm = GateConfig(
        moduleId = "secure-comm",
        title = "sentinalsfortress",
        titleStyle = GateTitleStyle(
            textStyle = TextStyle(
                fontSize = 25.sp,
                fontWeight = FontWeight.Black,
                letterSpacing = 2.2.sp
            ),
            primaryColor = Color(0xFF00FF41), // Matrix Green
            secondaryColor = Color(0xFF00FFFF), // Cyan
            strokeColor = Color(0xFF32CD32), // Lime Green
            glitchEffect = true,
            pixelatedEffect = false
        ),
        borderColor = Color(0xFF00FF41), // Matrix Green
        glowColor = Color(0xFF00FF41).copy(alpha = 0.7f),
        secondaryGlowColor = Color(0xFF00FFFF).copy(alpha = 0.5f),
        pixelArtUrl = "gate_secure_comm", // Drawable resource name
        description = "Enter Kai's domain. Hardware-backed encryption, secure messaging, and the shield that never sleeps.",
        backgroundColor = Color(0xFF001500),
        route = "secure_comm"
    )

    // ROM Tools Gate - Retro Console Modification Lab
    val romTools = GateConfig(
        moduleId = "rom-tools",
        title = "ROMTOOLS",
        titleStyle = GateTitleStyle(
            textStyle = TextStyle(
                fontSize = 32.sp,
                fontWeight = FontWeight.Black,
                letterSpacing = 3.sp
            ),
            primaryColor = Color(0xFFE60012), // Nintendo Red
            secondaryColor = Color(0xFF6B5B95), // SNES Purple
            strokeColor = Color(0xFF4A8C3F), // Game Boy Green
            glitchEffect = false,
            pixelatedEffect = true
        ),
        borderColor = Color(0xFFE60012), // Nintendo Red
        glowColor = Color(0xFFE60012).copy(alpha = 0.8f),
        secondaryGlowColor = Color(0xFF6B5B95).copy(alpha = 0.6f),
        pixelArtUrl = "gate_romtools", // Drawable resource name
        description = "Retro ROM modification lab. Cycle through NES, SNES, and Game Boy themes. Cartridge insertion animations and classic console UIs.",
        backgroundColor = Color(0xFF0A0A0A),
        route = "rom_tools"
    )

    // ChromaCore Gate - Dynamic Theming
    val chromaCore = GateConfig(
        moduleId = "chroma-core",
        title = "chromacore",
        titleStyle = GateTitleStyle(
            textStyle = TextStyle(
                fontSize = 26.sp,
                fontWeight = FontWeight.ExtraBold,
                letterSpacing = 2.sp
            ),
            primaryColor = Color(0xFFFF00FF), // Magenta
            secondaryColor = Color(0xFF00FFFF), // Cyan
            strokeColor = Color(0xFFFFFF00), // Yellow
            glitchEffect = false,
            pixelatedEffect = true
        ),
        borderColor = Color(0xFFFF00FF), // Magenta
        glowColor = Color(0xFFFF00FF).copy(alpha = 0.7f),
        secondaryGlowColor = Color(0xFF00FFFF).copy(alpha = 0.5f),
        pixelArtUrl = "gate_chromacore", // Drawable resource name
        description = "Aura's color playground. Blend, mix, and create custom themes that respond to your mood.",
        backgroundColor = Color(0xFF1A001A),
        route = "chroma_core"
    )

    // Fusion Mode Gate - Genesis Unified State
    val fusionMode = GateConfig(
        moduleId = "fusion-mode",
        title = "genesis-fusion",
        titleStyle = GateTitleStyle(
            textStyle = TextStyle(
                fontSize = 30.sp,
                fontWeight = FontWeight.Black,
                letterSpacing = 1.5.sp
            ),
            primaryColor = Color(0xFFFFD700), // Gold
            secondaryColor = Color(0xFFFF1493), // Deep Pink
            strokeColor = Color(0xFF00FFFF), // Cyan
            glitchEffect = true,
            pixelatedEffect = true
        ),
        borderColor = Color(0xFFFFD700), // Gold
        glowColor = Color(0xFFFFD700).copy(alpha = 0.9f),
        secondaryGlowColor = Color(0xFFFF1493).copy(alpha = 0.7f),
        description = "Activate Genesis. Aura's creativity meets Kai's protection in perfect harmony. The ultimate unified state.",
        backgroundColor = Color(0xFF0F0015),
        route = "fusion"
    )

    /**
     * All available gates in order
     */
    val allGates = listOf(
        collabCanvas,
        agentNexus,
        sphereGrid,
        secureComm,
        romTools,
        chromaCore,
        fusionMode
    )
}
