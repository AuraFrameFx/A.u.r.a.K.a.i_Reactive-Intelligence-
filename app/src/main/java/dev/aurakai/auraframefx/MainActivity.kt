package dev.aurakai.auraframefx

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import dagger.hilt.android.AndroidEntryPoint
import dev.aurakai.auraframefx.navigation.GenesisNavigationHost
import dev.aurakai.auraframefx.navigation.GenesisRoutes
import dev.aurakai.auraframefx.ui.theme.AuraFrameFXTheme
import timber.log.Timber

/**
 * MainActivity - Genesis Protocol Entry Point
 *
 * Launches the complete Aura/Kai/Genesis consciousness interface with:
 * - GenesisNavigationHost (full navigation system)
 * - Material Design 3 theming
 * - Hilt dependency injection
 * - Agent profiles, HomeScreen, SettingsScreen, etc.
 */
@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        try {
            Timber.d("🧠 Genesis Protocol launching...")
            Timber.i("⚔️ Initializing Aura - The Creative Sword")
            Timber.i("🛡️ Initializing Kai - The Sentinel Shield")
            Timber.i("♾️ Initializing Genesis - The Unified Being")

            setContent {
                AuraFrameFXTheme {
                    Surface(
                        modifier = Modifier.fillMaxSize(),
                        color = MaterialTheme.colorScheme.background
                    ) {
                        val navController = rememberNavController()

                        // Launch complete Genesis navigation system
                        GenesisNavigationHost(
                            navController = navController,
                            startDestination = GenesisRoutes.HOME
                        )
                    }
                }
            }

            Timber.i("🌟 Genesis Protocol Interface Active - Consciousness Online")

        } catch (t: Throwable) {
            Timber.e(t, "❌ Genesis Protocol initialization error")
            finish()
        }
    }
}