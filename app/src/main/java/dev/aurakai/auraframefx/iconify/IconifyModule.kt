package dev.aurakai.auraframefx.iconify

import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

/**
 * 🎨 Iconify Hilt Module
 *
 * Provides dependency injection for Iconify services.
 */
@Module
@InstallIn(SingletonComponent::class)
object IconifyModule {

    @Provides
    @Singleton
    fun provideIconCacheManager(
        @ApplicationContext context: Context
    ): IconCacheManager {
        return IconCacheManager(context)
    }

    @Provides
    @Singleton
    fun provideIconifyOkHttpClient(): OkHttpClient {
        return OkHttpClient.Builder()
            .connectTimeout(15, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .addInterceptor { chain ->
                val request = chain.request().newBuilder()
                    .addHeader("User-Agent", "AuraKai-Iconify/1.0")
                    .build()
                chain.proceed(request)
            }
            .build()
    }

    @Provides
    @Singleton
    fun provideIconifyService(
        okHttpClient: OkHttpClient,
        iconCacheManager: IconCacheManager
    ): IconifyService {
        return IconifyService(okHttpClient, iconCacheManager)
    }
}
