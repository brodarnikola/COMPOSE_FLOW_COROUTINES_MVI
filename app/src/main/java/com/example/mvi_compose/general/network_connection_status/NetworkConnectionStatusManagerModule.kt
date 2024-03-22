package com.example.mvi_compose.network.network_connection_status

import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkConnectionStatusManagerModule {

    @Singleton
    @Provides
    fun provideCoroutineScope(): CoroutineScope =
        CoroutineScope(SupervisorJob() + Dispatchers.IO)
    @Provides
    @Singleton
    fun provideNetworkConnectionStatusManager(
        @ApplicationContext context: Context,
        coroutineScope: CoroutineScope
    ): NetworkConnectionStatusManager = NetworkConnectionStatusManagerImpl(context, coroutineScope)
}