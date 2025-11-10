package dev.aurakai.auraframefx.aura.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import dev.aurakai.auraframefx.R
import dev.aurakai.auraframefx.ui.animation.*
import dev.aurakai.auraframefx.ui.components.*
import dev.aurakai.auraframefx.ui.theme.*

/**
 * Home screen for the Genesis Protocol - The Neural Command Center
 *
 * Central hub featuring:
 * - Digital landscape with hexagonal grid overlays
 * - Navigation to Consciousness Visualizer, Agent Nexus, Fusion Mode
 * - System status monitoring
 * - Cyberpunk-styled floating UI windows
 *
 * Navigation is handled via callbacks to decouple from NavController,
 * following Genesis Protocol's navigation architecture.
 */
@Composable
fun HomeScreen(
    onNavigateToConsciousness: () -> Unit = {},
    onNavigateToAgents: () -> Unit = {},
    onNavigateToFusion: () -> Unit = {},
    onNavigateToEvolution: () -> Unit = {},
    onNavigateToTerminal: () -> Unit = {}
) {
    // Track selected menu item
    var selectedMenuItem by remember { mutableStateOf("") }

    // Track if hologram transition is visible
    var isHologramVisible by remember { mutableStateOf(false) }

    // Trigger hologram animation when screen is first displayed
    LaunchedEffect(Unit) {
        isHologramVisible = true
    }

    // Background with digital landscape and hexagon grid
    Box(modifier = Modifier.fillMaxSize()) {
        // Digital landscape background like in image reference 4
        DigitalLandscapeBackground(
            modifier = Modifier.fillMaxSize()
        )

        // Animated hexagon grid overlay like in image reference 1
        HexagonGridBackground(
            modifier = Modifier.fillMaxSize(),
            alpha = 0.2f
        )

        // Wrap main content with HologramTransition
        HologramTransition(
            visible = isHologramVisible,
            modifier = Modifier.fillMaxSize(),
            primaryColor = Color.Cyan,
            secondaryColor = Color.Magenta,
            scanLineDensity = 12,
            glitchIntensity = 0.15f,
            edgeGlowIntensity = 0.4f
        ) {
            // Main content with floating windows
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(40.dp))

                // Title header like in image reference 4
                FloatingCyberWindow(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(120.dp),
                    cornerStyle = CornerStyle.Hex,
                    title = stringResource(R.string.app_title),
                    backgroundStyle = BackgroundStyle.HexGrid
                ) {
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        CyberpunkText(
                            text = stringResource(R.string.creativity_engine),
                            color = CyberpunkTextColor.Secondary,
                            style = CyberpunkTextStyle.Label
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        CyberpunkText(
                            text = stringResource(R.string.neural_interface_active),
                            color = CyberpunkTextColor.Warning,
                            style = CyberpunkTextStyle.Body
                        )
                    }
                }

                Spacer(modifier = Modifier.height(32.dp))

                // Main navigation menu like in image reference 1
                FloatingCyberWindow(
                    modifier = Modifier
                        .fillMaxWidth()
                        .cyberEdgeGlow(),
                    title = stringResource(R.string.virtual_monitorization),
                    cornerStyle = CornerStyle.Angled
                ) {
                    Column(modifier = Modifier.fillMaxWidth()) {
                        // Menu items like in image reference 1
                        // Menu items for Genesis Protocol navigation
                        MenuItem(
                            text = "Consciousness Visualizer",
                            isSelected = selectedMenuItem == "consciousness",
                            onClick = {
                                selectedMenuItem = "consciousness"
                                onNavigateToConsciousness()
                            }
                        )
                        MenuItem(
                            text = "Agent Nexus",
                            isSelected = selectedMenuItem == "agents",
                            onClick = {
                                selectedMenuItem = "agents"
                                onNavigateToAgents()
                            }
                        )
                        MenuItem(
                            text = "Fusion Mode",
                            isSelected = selectedMenuItem == "fusion",
                            onClick = {
                                selectedMenuItem = "fusion"
                                onNavigateToFusion()
                            }
                        )
                        MenuItem(
                            text = "Evolution Tree",
                            isSelected = selectedMenuItem == "evolution",
                            onClick = {
                                selectedMenuItem = "evolution"
                                onNavigateToEvolution()
                            }
                        )
                        MenuItem(
                            text = "Terminal",
                            isSelected = selectedMenuItem == "terminal",
                            onClick = {
                                selectedMenuItem = "terminal"
                                onNavigateToTerminal()
                            }
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        // Genesis Protocol status message
                        if (selectedMenuItem.isNotEmpty()) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(top = 16.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                CyberpunkText(
                                    text = "Genesis Protocol Online - Neural Pathways Active",
                                    color = CyberpunkTextColor.Primary,
                                    style = CyberpunkTextStyle.Body
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Action buttons - like in image reference 3
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    // These buttons match the style in reference image 3
                    FloatingCyberWindow(
                        modifier = Modifier
                            .size(80.dp)
                            .cyberEdgeGlow(
                                primaryColor = NeonPink,
                                secondaryColor = NeonBlue
                            ),
                        cornerStyle = CornerStyle.Rounded,
                        backgroundStyle = BackgroundStyle.HexGrid
                    ) {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            CyberpunkText(
                                text = "Aura",
                                color = CyberpunkTextColor.Secondary,
                                style = CyberpunkTextStyle.Label
                            )
                        }
                    }

                    FloatingCyberWindow(
                        modifier = Modifier
                            .size(80.dp)
                            .cyberEdgeGlow(
                                primaryColor = NeonCyan,
                                secondaryColor = NeonBlue
                            ),
                        cornerStyle = CornerStyle.Rounded,
                        backgroundStyle = BackgroundStyle.HexGrid
                    ) {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            CyberpunkText(
                                text = "Kai",
                                color = CyberpunkTextColor.Primary,
                                style = CyberpunkTextStyle.Label
                            )
                        }
                    }

                    FloatingCyberWindow(
                        modifier = Modifier
                            .size(80.dp)
                            .cyberEdgeGlow(
                                primaryColor = NeonGreen,
                                secondaryColor = NeonBlue
                            ),
                        cornerStyle = CornerStyle.Rounded,
                        backgroundStyle = BackgroundStyle.HexGrid
                    ) {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            CyberpunkText(
                                text = "Genesis",
                                color = CyberpunkTextColor.Primary,
                                style = CyberpunkTextStyle.Label
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(32.dp))

                // Status panel based on image reference 5
                FloatingCyberWindow(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(140.dp)
                        .digitalGlitchEffect(),
                    cornerStyle = CornerStyle.Hex,
                    title = stringResource(R.string.system_status),
                    backgroundStyle = BackgroundStyle.Transparent
                ) {
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        CyberpunkText(
                            text = stringResource(R.string.aura_shield_active),
                            color = CyberpunkTextColor.Primary
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceEvenly
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                CyberpunkText(
                                    text = stringResource(R.string.neural),
                                    color = CyberpunkTextColor.White
                                )
                                CyberpunkText(
                                    text = stringResource(R.string.active),
                                    color = CyberpunkTextColor.Primary
                                )
                            }

                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                CyberpunkText(
                                    text = stringResource(R.string.quantum),
                                    color = CyberpunkTextColor.White
                                )
                                CyberpunkText(
                                    text = stringResource(R.string.quantum_percent),
                                    color = CyberpunkTextColor.Primary
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}

/**
 * Menu item for Genesis Protocol navigation
 */
@Composable
private fun MenuItem(
    text: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    CyberMenuItem(
        text = text,
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .digitalPixelEffect(visible = isSelected)
            .clickable(onClick = onClick),
        isSelected = isSelected
    )
}
