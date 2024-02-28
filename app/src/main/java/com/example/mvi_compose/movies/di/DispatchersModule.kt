/*
 * Copyright © 2022 Sunbird. All rights reserved.
 *
 * Sunbird Secure Messaging
 *
 * Created by Cinnamon.
 */
package com.example.mvi_compose.movies.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.Dispatchers

@Module
@InstallIn(SingletonComponent::class)
object DispatchersModule {
    @Provides @DefaultDispatcher fun provideDefaultDispatcher() = Dispatchers.Default

    @Provides @IODispatcher fun provideIODispatcher() = Dispatchers.IO

    @Provides @MainDispatcher fun provideMainDispatcher() = Dispatchers.Main
}
