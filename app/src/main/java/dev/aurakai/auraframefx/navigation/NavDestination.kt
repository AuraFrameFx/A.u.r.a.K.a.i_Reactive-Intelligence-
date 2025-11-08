package dev.aurakai.auraframefx.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Brush
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Message
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.ui.graphics.vector.ImageVector

sealed class NavDestination(val route: String, val title: String, val icon: ImageVector?) {
    // Main Navigation
    object Home : NavDestination("home", "Home", Icons.Filled.Home)
    object AiChat : NavDestination("ai_chat", "AI Chat", Icons.Filled.Message)
    object Profile : NavDestination("profile", "Profile", Icons.Filled.Person)
    object Settings : NavDestination("settings", "Settings", Icons.Filled.Settings)

    // Gamification & Progression Screens
    object AgentNexus : NavDestination("agent_nexus", "Agent Nexus", Icons.Filled.Person)
    object DataVein : NavDestination("data_vein", "DataVein", null)
    object SphereGrid : NavDestination("sphere_grid", "Sphere Grid", null)
    object FusionMode : NavDestination("fusion", "Fusion Mode", null)
    object Consciousness : NavDestination("consciousness", "Consciousness", null)
    object Evolution : NavDestination("evolution", "Evolution", null)

    // Features
    object Canvas : NavDestination("canvas", "Canvas", Icons.Filled.Brush)
    object OracleDriveControl : NavDestination("oracle_drive_control", "Oracle Drive", Icons.Filled.Folder)

    // Gate Navigation - Module Selection Hub
    object Gates : NavDestination("gates", "Gates", null)

    // Journal PDA - Retro Gaming Wellness Hub
    object JournalPDA : NavDestination("journal_pda", "Journal", null)

    companion object {
        val bottomNavItems = listOf(Home, AgentNexus, AiChat, Canvas, Settings)
        val gamificationScreens = listOf(AgentNexus, SphereGrid, FusionMode, Consciousness, Evolution)
    }
}
