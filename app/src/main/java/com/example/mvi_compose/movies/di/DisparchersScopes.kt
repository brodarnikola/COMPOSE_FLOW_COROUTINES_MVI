/*
 * Copyright © 2022 Sunbird. All rights reserved.
 *
 * Sunbird Secure Messaging
 *
 * Created by Cinnamon.
 */
package com.example.mvi_compose.movies.di

import javax.inject.Qualifier

@Retention(AnnotationRetention.BINARY) @Qualifier annotation class DefaultDispatcher

@Retention(AnnotationRetention.BINARY) @Qualifier annotation class IODispatcher

@Retention(AnnotationRetention.BINARY) @Qualifier annotation class MainDispatcher
