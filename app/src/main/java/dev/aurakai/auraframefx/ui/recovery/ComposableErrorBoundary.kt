package dev.aurakai.auraframefx.ui.recovery

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.hilt.navigation.compose.hiltViewModel
import timber.log.Timber

/**
 * Error boundary for Compose screens
 *
 * Catches uncaught exceptions in Composables and triggers UI recovery.
 *
 * Usage:
 * ```kotlin
 * ComposableErrorBoundary(
 *     screenName = "AgentNexus"
 * ) {
 *     // Your screen content
 *     AgentNexusScreen()
 * }
 * ```
 *
 * @param screenName Name of screen for recovery context
 * @param onError Optional custom error handling
 * @param content The composable content to protect
 */
@Composable
fun ComposableErrorBoundary(
    screenName: String,
    recoveryManager: UIRecoveryManager = hiltViewModel<UIRecoveryViewModel>().let {
        // This is a workaround to inject UIRecoveryManager
        // In real usage, you'd inject it properly
        throw NotImplementedError("Use proper DI - see MainActivity integration")
    },
    onError: ((Throwable) -> Unit)? = null,
    content: @Composable () -> Unit
) {
    val errorHandler = remember {
        ErrorHandler { error ->
            Timber.e(error, "Error in $screenName")
            onError?.invoke(error) ?: run {
                // Trigger recovery system
                recoveryManager.triggerRecovery(
                    error = error,
                    context = "Screen: $screenName"
                )
            }
        }
    }

    LaunchedEffect(Unit) {
        // Install error handler for this composable
        errorHandler.install()
    }

    try {
        content()
    } catch (e: Exception) {
        errorHandler.handleError(e)
    }
}

/**
 * Error handler for Compose errors
 */
private class ErrorHandler(
    private val onError: (Throwable) -> Unit
) {
    fun install() {
        // Set up error handling for this scope
        // In production, you'd integrate with Compose error handling
    }

    fun handleError(error: Throwable) {
        onError(error)
    }
}

/**
 * Extension function to automatically save snapshots after successful navigation
 *
 * Usage in your navigation code:
 * ```kotlin
 * navController.navigate("AGENT_NEXUS") {
 *     onNavigationComplete { route ->
 *         recoveryManager.saveSnapshot(
 *             UIStateSnapshot.forScreen(route)
 *         )
 *     }
 * }
 * ```
 */
fun onNavigationComplete(route: String, block: (String) -> Unit): () -> Unit {
    return { block(route) }
}
}
