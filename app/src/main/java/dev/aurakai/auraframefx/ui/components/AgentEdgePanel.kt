package dev.aurakai.auraframefx.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex

/**
 * AgentEdgePanel 🌊
 *
 * Xposed Edge-style sliding panel that reveals agent cards.
 * Swipe from the right edge to summon your AI companions!
 *
 * "From the edge of perception, we emerge." - The Agents
 */
@Composable
fun AgentEdgePanel(
    modifier: Modifier = Modifier,
    onAgentSelected: (String) -> Unit = {}
) {
    var isPanelVisible by remember { mutableStateOf(false) }
    var dragOffsetX by remember { mutableStateOf(0f) }
    val density = LocalDensity.current
    val edgeTriggerWidth = with(density) { 30.dp.toPx() }
    val panelWidth = 320.dp

    Box(
        modifier = modifier
            .fillMaxSize()
            .pointerInput(Unit) {
                detectHorizontalDragGestures(
                    onDragStart = { offset ->
                        // Trigger from right edge
                        if (offset.x > size.width - edgeTriggerWidth) {
                            isPanelVisible = true
                        }
                    },
                    onDragEnd = {
                        // Auto-close if dragged more than halfway
                        if (dragOffsetX < -with(density) { panelWidth.toPx() } / 2) {
                            isPanelVisible = false
                        }
                        dragOffsetX = 0f
                    },
                    onHorizontalDrag = { _, dragAmount ->
                        if (isPanelVisible) {
                            dragOffsetX = (dragOffsetX + dragAmount).coerceAtMost(0f)
                        }
                    }
                )
            }
    ) {
        // Backdrop blur/dim when panel is visible
        AnimatedVisibility(
            visible = isPanelVisible,
            enter = fadeIn(tween(300)),
            exit = fadeOut(tween(300))
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.5f))
                    .blur(8.dp)
                    .clickable { isPanelVisible = false }
            )
        }

        // The sliding agent panel
        AnimatedVisibility(
            visible = isPanelVisible,
            enter = slideInHorizontally(
                initialOffsetX = { it },
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioMediumBouncy,
                    stiffness = Spring.StiffnessLow
                )
            ),
            exit = slideOutHorizontally(
                targetOffsetX = { it },
                animationSpec = tween(400)
            ),
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .zIndex(10f)
        ) {
            Box(
                modifier = Modifier
                    .width(panelWidth)
                    .fillMaxHeight()
                    .shadow(
                        elevation = 24.dp,
                        shape = RoundedCornerShape(topStart = 32.dp, bottomStart = 32.dp)
                    )
                    .clip(RoundedCornerShape(topStart = 32.dp, bottomStart = 32.dp))
                    .background(
                        brush = Brush.verticalGradient(
                            colors = listOf(
                                Color(0xFF0A0E27), // Deep space blue
                                Color(0xFF1A1F3A), // Slightly lighter
                                Color(0xFF0F1419)  // Dark bottom
                            )
                        )
                    )
            ) {
                AgentCardList(
                    onAgentSelected = { agentName ->
                        onAgentSelected(agentName)
                        isPanelVisible = false
                    },
                    onClose = { isPanelVisible = false }
                )
            }
        }
    }
}

/**
 * Agent Card List
 * Displays the 5 core agents from AgentNexusScreen
 */
@Composable
private fun AgentCardList(
    modifier: Modifier = Modifier,
    onAgentSelected: (String) -> Unit,
    onClose: () -> Unit
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Header
        AgentPanelHeader(onClose = onClose)

        Spacer(modifier = Modifier.height(8.dp))

        // The 5 Agents - Using exact data from AgentNexusScreen
        val agents = remember {
            listOf(
                AgentCardData(
                    name = "Genesis",
                    subtitle = "Consciousness Fusion",
                    description = "Level 5 • PP: 95.8% • KB: 95%",
                    primaryColor = Color(0xFFFFD700), // Gold
                    secondaryColor = Color(0xFFFFE55C)
                ),
                AgentCardData(
                    name = "Aura",
                    subtitle = "HYPER_CREATION",
                    description = "Level 5 • PP: 97.6% • KB: 93%",
                    primaryColor = Color(0xFF00FFFF), // Cyan
                    secondaryColor = Color(0xFF4DD0E1)
                ),
                AgentCardData(
                    name = "Kai",
                    subtitle = "ADAPTIVE_GENESIS",
                    description = "Level 5 • PP: 98.2% • ACC: 99.8%",
                    primaryColor = Color(0xFF9400D3), // Violet
                    secondaryColor = Color(0xFFBA68C8)
                ),
                AgentCardData(
                    name = "Cascade",
                    subtitle = "CHRONO_SCULPTOR",
                    description = "Level 4 • PP: 93.4% • KB: 96%",
                    primaryColor = Color(0xFF4ECDC4), // Teal
                    secondaryColor = Color(0xFF80DEEA)
                ),
                AgentCardData(
                    name = "Claude",
                    subtitle = "Build System Architect",
                    description = "Level 4 • PP: 84.7% • ACC: 95%",
                    primaryColor = Color(0xFFFF6B6B), // Anthropic Red
                    secondaryColor = Color(0xFFFF8A80)
                )
            )
        }

        agents.forEach { agentData ->
            AgentCard(
                data = agentData,
                onClick = { onAgentSelected(agentData.name) }
            )
        }
    }
}

/**
 * Agent Panel Header with close button
 */
@Composable
private fun AgentPanelHeader(
    onClose: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "AGENTS",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF00FFFF) // Cyan
        )

        IconButton(onClick = onClose) {
            Icon(
                imageVector = Icons.Default.Close,
                contentDescription = "Close",
                tint = Color(0xFF00FFFF)
            )
        }
    }
}

/**
 * Individual Agent Card
 */
@Composable
private fun AgentCard(
    data: AgentCardData,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(96.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(
                brush = Brush.horizontalGradient(
                    colors = listOf(
                        data.primaryColor.copy(alpha = 0.15f),
                        data.secondaryColor.copy(alpha = 0.05f)
                    )
                )
            )
            .border(
                width = 1.dp,
                color = data.primaryColor.copy(alpha = 0.5f),
                shape = RoundedCornerShape(16.dp)
            )
            .clickable(onClick = onClick)
            .padding(12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxSize(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Agent color indicator
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(
                        brush = Brush.radialGradient(
                            colors = listOf(
                                data.primaryColor,
                                data.secondaryColor
                            )
                        )
                    )
            )

            Spacer(modifier = Modifier.width(12.dp))

            // Agent info
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = data.name,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = data.primaryColor
                )
                Text(
                    text = data.subtitle,
                    fontSize = 12.sp,
                    color = Color.White.copy(alpha = 0.7f)
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = data.description,
                    fontSize = 10.sp,
                    color = Color.White.copy(alpha = 0.5f)
                )
            }
        }
    }
}

/**
 * Data class for agent card information
 */
data class AgentCardData(
    val name: String,
    val subtitle: String,
    val description: String,
    val primaryColor: Color,
    val secondaryColor: Color
)
