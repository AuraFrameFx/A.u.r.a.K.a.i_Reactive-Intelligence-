package dev.aurakai.auraframefx.di

import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import dev.aurakai.auraframefx.state.AppStateManager
import javax.inject.Named
import javax.inject.Singleton

/**
 * Hilt Module for providing application state related dependencies.
 *
 * This module provides a separate DataStore instance for app-level state
 * (distinct from user preferences) and the AppStateManager that uses it.
 *
 * Note: This is separate from DataStoreModule which provides user preferences.
 */
@Module
@InstallIn(SingletonComponent::class)
object AppStateModule {

    /**
     * Provides a DataStore instance for application state.
     *
     * This DataStore is separate from user preferences and stores app-level state
     * such as onboarding completion, feature flags, and app configuration.
     *
     * @param context Application context provided by Hilt for creating the DataStore
     * @return A placeholder DataStore instance (currently using Any type)
     *
     * TODO: Replace Any with actual DataStore<Preferences> type when implementing:
     * ```
     * return androidx.datastore.preferences.core.PreferenceDataStoreFactory.create(
     *     produceFile = { context.preferencesDataStoreFile("app_state_settings") }
     * )
     * ```
     */
    @Provides
    @Singleton
    @Named("AppStateDataStore")
    fun provideDataStore(@ApplicationContext context: Context): Any {
        // Placeholder implementation - context will be used when creating actual DataStore
        // Context is intentionally unused in placeholder to avoid premature initialization
        return Any()
    }

    /**
     * Provides an AppStateManager for managing application-level state.
     *
     * @param dataStore The app state DataStore instance (currently a placeholder)
     * @return An AppStateManager instance
     */
    @Provides
    @Singleton
    fun provideAppStateManager(@Named("AppStateDataStore") dataStore: Any): AppStateManager {
        // AppStateManager is instantiated without dataStore dependency in placeholder
        // Will be updated when dataStore is properly implemented
        return AppStateManager()
    }
}
