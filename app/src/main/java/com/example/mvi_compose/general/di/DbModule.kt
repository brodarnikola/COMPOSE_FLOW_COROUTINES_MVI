package com.example.mvi_compose.general.utils

import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class DbModule{

    @Provides
    @Singleton
    internal fun provideMovieDb(@ApplicationContext context: Context): MovieDb {
        return Room.databaseBuilder(context, MovieDb::class.java, AppConstants.DB_NAME).fallbackToDestructiveMigration()
            .build()
    }

    @Provides
    @Singleton
    internal fun provideMovieDao(movieDb:MovieDb): MovieDao {
        return movieDb.movieDao()
    }
}
