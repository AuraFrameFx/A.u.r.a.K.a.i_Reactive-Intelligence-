package dev.aurakai.auraframefx

import timber.log.Timber

/**
 * YukiHook API initializer for Xposed module integration.
 *
 * This class serves as the entry point for the YukiHook framework,
 * which provides a modern Kotlin-based API for Xposed module development.
 *
 * When Genesis Protocol is used as an Xposed module, this initializer
 * delegates to GenesisHookEntry for actual hook implementation.
 */
class YukiHookApiInitializer : IYukiHookXposedInit {
    /**
     * Initializes the YukiHook framework and registers system hooks.
     *
     * Called by the Xposed framework when the module is loaded.
     * Delegates to GenesisHookEntry for actual hook registration.
     */
    fun onInit() {
        try {
            Timber.i("YukiHookApiInitializer: Initializing Genesis Protocol Xposed module")

            // Delegate to GenesisHookEntry for hook registration
            dev.aurakai.auraframefx.xposed.GenesisHookEntry.onInit()

            Timber.i("YukiHookApiInitializer: Genesis Protocol hooks registered successfully")
        } catch (e: Exception) {
            Timber.e(e, "YukiHookApiInitializer: Failed to initialize Xposed module")
        }
    }
}

/**
 * Annotation marker for YukiHook Xposed initialization classes.
 *
 * Classes annotated with this marker are recognized by the YukiHook
 * framework as entry points for Xposed module initialization.
 */
@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
annotation class IYukiHookXposedInit
