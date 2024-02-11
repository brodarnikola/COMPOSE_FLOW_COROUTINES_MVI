package com.example.mvi_compose.movies.utils

import com.example.mvi_compose.movies.details.TrailerApi
import com.example.mvi_compose.movies.movies_list.IMovieRepo
import com.example.mvi_compose.movies.movies_list.MovieApi
import com.example.mvi_compose.movies.movies_list.MovieRepo
import com.squareup.moshi.Moshi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class RepositoryModule {

    @Singleton
    @Provides
    fun provideMovieRepository(
        movieDao: MovieDao,
        moviesApi: MovieApi,
        trailersApi: TrailerApi,
        moshi: Moshi
    ): IMovieRepo {
        return MovieRepo(movieDao, moviesApi, trailersApi, moshi)
    }
}